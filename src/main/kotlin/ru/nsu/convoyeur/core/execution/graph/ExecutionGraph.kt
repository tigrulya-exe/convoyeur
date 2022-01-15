package ru.nsu.convoyeur.core.execution.graph

import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode

data class ExecutionGraph(
    val nodes: Map<String, ExecutionGraphNode<*, *>>,
    val sourceIds: List<String>,
)