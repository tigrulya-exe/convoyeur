package ru.nsu.convoyeur.api.execution.graph.transform

import ru.nsu.convoyeur.api.declaration.GraphNode
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode

interface GraphNodeTransformer {
    fun <S, D> transform(
        node: GraphNode<S, D>,
        context: MutableExecutionContext<S, D>
    ): ExecutionGraphNode<out S, out D>
}