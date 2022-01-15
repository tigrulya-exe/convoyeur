package ru.nsu.convoyeur.core.execution.graph

import ru.nsu.convoyeur.api.declaration.GraphNode
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ContextEnrichedAction
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphNodeBuilder
import ru.nsu.convoyeur.core.declaration.graph.SinkNode
import ru.nsu.convoyeur.core.declaration.graph.SourceNode
import ru.nsu.convoyeur.core.declaration.graph.TransformNode

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
            is SinkNode -> suspend { node.consumer(context) }
            is TransformNode -> suspend { node.transform(context) }
            else -> throw RuntimeException("Maybe turn GraphNode to sealed class?")
        }
    }
}