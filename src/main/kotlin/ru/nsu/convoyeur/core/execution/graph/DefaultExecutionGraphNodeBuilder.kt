package ru.nsu.convoyeur.core.execution.graph

import kotlinx.coroutines.selects.select
import ru.nsu.convoyeur.api.declaration.GraphNode
import ru.nsu.convoyeur.api.declaration.StatefulGraphNode
import ru.nsu.convoyeur.api.declaration.StatelessConsumerNode
import ru.nsu.convoyeur.api.execution.context.ConsumerExecutionContext
import ru.nsu.convoyeur.api.execution.context.ExecutionContext
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ContextEnrichedAction
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphNodeBuilder

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
            is StatefulGraphNode<S, D, *> -> wrapStatefulAction(node, context)
            is StatelessConsumerNode<S, D, *> -> wrapStatelessAction(node, context)
            else -> throw RuntimeException("Maybe turn GraphNode to sealed class?")
        }
    }

    private fun <E : ExecutionContext, S, D> wrapStatefulAction(
        node: StatefulGraphNode<S, D, E>,
        context: MutableExecutionContext<S, *>,
    ): ContextEnrichedAction = suspend {
        node.action(context as E)
    }

    private fun <E : ConsumerExecutionContext<S>, S> wrapStatelessAction(
        node: StatelessConsumerNode<S, *, E>,
        context: MutableExecutionContext<S, *>,
    ): ContextEnrichedAction = suspend {
        with(context as E) {
            val openChannels = HashSet(context.inputChannels.keys)
            while (isActive && openChannels.isNotEmpty()) {
                val channelNameValue = select<Pair<String, S?>> {
                    context.inputChannels
                        .filter { openChannels.contains(it.key) }
                        .forEach {
                            it.value.onReceiveCatching { result ->
                                it.key to result.getOrNull()
                            }
                        }
                }

                if (channelNameValue.second == null) {
                    openChannels.remove(channelNameValue.first)
                    node.onChannelClose(context, channelNameValue.first)
                    continue
                }
                node.action(context, channelNameValue.first, channelNameValue.second!!)
            }
        }
    }
}