package ru.nsu.convoyeur.api.execution.graph.transform

import ru.nsu.convoyeur.api.declaration.graph.GraphNode
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode

interface ExecutionGraphNodeBuilder {
    fun <S, D> build(
        node: GraphNode<S, D>,
        context: MutableExecutionContext<S, D>
    ): ExecutionGraphNode<S, D>
}