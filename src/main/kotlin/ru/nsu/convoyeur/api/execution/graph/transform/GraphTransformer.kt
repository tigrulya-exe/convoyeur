package ru.nsu.convoyeur.api.execution.graph.transform

import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.core.declaration.graph.SourceNode

interface GraphTransformer {
    fun <V> transform(source: SourceNode<V>): ExecutionGraphNode<Nothing, V>

    fun <V> transform(sources: List<SourceNode<V>>): List<ExecutionGraphNode<Nothing, V>>
}