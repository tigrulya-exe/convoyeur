package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.ConsumerNode
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.api.execution.context.SourceExecutionContext
import java.util.*

open class SourceNode<V>(
    override val id: String,
    val producer: suspend SourceExecutionContext<V>.() -> Unit,
    override var outputNodes: List<ConsumerNode<V, *>> = listOf(),
) : SourceGraphNode<V>

suspend fun <V> SourceExecutionContext<V>.emit(nodeId: String, value: V) {
    outputChannel(nodeId)?.send(value)
}

/**
 * Emits to first output channel
 */
suspend fun <V> SourceExecutionContext<V>.emit(value: V) {
    outputChannels().values.firstOrNull()?.send(value)
}

fun <V> Iterable<V>.asSourceNode(): SourceGraphNode<V> {
    return SourceNode(
        id = UUID.randomUUID().toString(),
        producer = {
            this@asSourceNode.forEach { emit(it) }
        }
    )
}

fun <V> Iterable<V>.asSourceNode(outputChannelName: String): SourceGraphNode<V> {
    return SourceNode(
        id = UUID.randomUUID().toString(),
        producer = {
            this@asSourceNode.forEach { emit(outputChannelName, it) }
        }
    )
}
