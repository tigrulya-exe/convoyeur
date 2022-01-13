package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.GraphNode
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.api.execution.context.SourceExecutionContext

open class SourceNode<V>(
    override val id: String,
    val producer: suspend SourceExecutionContext<V>.() -> Unit,
    override var outputNodes: List<GraphNode<V, *>> = listOf(),
) : SourceGraphNode<V>

suspend fun <V> SourceExecutionContext<V>.emit(nodeId: String, value: V) {
    outputChannel(nodeId)?.put(value)
}