package ru.nsu.convoyeur.api.execution.graph.transform

import ru.nsu.convoyeur.api.declaration.GraphNode
import ru.nsu.convoyeur.api.declaration.SinkNode
import ru.nsu.convoyeur.api.declaration.SourceNode
import ru.nsu.convoyeur.api.declaration.TransformNode
import ru.nsu.convoyeur.api.execution.context.ExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.ExecutionNode

interface NodeTransformer {
    fun <S, D> transform(
        node: GraphNode<S, D>,
        context: ExecutionContext<S, D>
    ): ExecutionNode<out S, out D>

    companion object {
        fun create(): NodeTransformer {
            return DefaultNodesTransformer()
        }
    }
}

class DefaultNodesTransformer : NodeTransformer {
    override fun <S, D> transform(
        node: GraphNode<S, D>,
        context: ExecutionContext<S, D>
    ): ExecutionNode<out S, out D> {
        return when (node) {
            is SourceNode -> ExecutionGraphNode(node.id, { node.producer(context) })
            is SinkNode -> ExecutionGraphNode(node.id, { node.consumer(context) })
            is TransformNode -> ExecutionGraphNode(node.id, { node.transform(context) })
            else -> throw RuntimeException("Maybe turn GraphNode to sealed class?")
        }
    }
}