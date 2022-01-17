package ru.nsu.convoyeur.core.execution.graph

import kotlinx.coroutines.selects.select
import ru.nsu.convoyeur.api.declaration.GraphNode
import ru.nsu.convoyeur.api.execution.context.MutableConsumerExecutionContext
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ContextEnrichedAction
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphNodeBuilder
import ru.nsu.convoyeur.core.declaration.graph.*

class DefaultExecutionGraphNodeBuilder : ExecutionGraphNodeBuilder {
    override fun <S, D> build(
        node: GraphNode<S, D>,
        context: MutableExecutionContext<S, D>
    ): ExecutionGraphNode<S, D> {
        return ExecutionGraphNodeImpl(
            id = node.id,
            action = enrichActionWithContext(node, context),
            context = context
        )
    }

    private fun <S, D> enrichActionWithContext(
        node: GraphNode<S, D>,
        context: MutableExecutionContext<S, D>
    ): ContextEnrichedAction {
        return when (node) {
            is SourceNode -> suspend { node.producer(context) }
            is StatefulSinkNode -> suspend { node.consumer(context) }
            is StatefulTransformNode -> suspend { node.transform(context) }
            is SinkNode -> wrapWithSelect(context, node.callback)
            is TransformNode -> wrapWithSelect(context, node.callback)
            else -> throw RuntimeException("Maybe turn GraphNode to sealed class?")
        }
    }

    private fun <E : MutableConsumerExecutionContext<S>, S> wrapWithSelect(
        context: MutableConsumerExecutionContext<S>,
        action: suspend E.(String, S?) -> Unit
    ): ContextEnrichedAction = {
        with(context) {
            while (isActive && hasOpenInputChannels) {
                val inputPair = select<Pair<String, S?>> {
                    context.inputChannels
                        .filter { !it.value.isClosedForReceive }
                        .forEach {
                            it.value.onReceiveCatching { result ->
                                it.key to result.getOrNull()
                            }
                        }
                }
                action(context as E, inputPair.first, inputPair.second)
            }
        }
    }

}