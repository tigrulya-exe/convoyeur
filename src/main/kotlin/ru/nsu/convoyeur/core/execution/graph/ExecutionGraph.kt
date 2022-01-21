package ru.nsu.convoyeur.core.execution.graph

import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import java.util.*

data class ExecutionGraph(
    val id: String = UUID.randomUUID().toString(),
    val nodes: Map<String, ExecutionGraphNode<*, *>>,
    val sourceIds: List<String>,
)