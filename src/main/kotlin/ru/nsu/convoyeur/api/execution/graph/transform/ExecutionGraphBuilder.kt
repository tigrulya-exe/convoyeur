package ru.nsu.convoyeur.api.execution.graph.transform

import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.core.execution.graph.ExecutionGraph

interface ExecutionGraphBuilder {
    fun <V> build(source: SourceGraphNode<V>): ExecutionGraph

    fun <V> build(sources: List<SourceGraphNode<V>>): ExecutionGraph
}