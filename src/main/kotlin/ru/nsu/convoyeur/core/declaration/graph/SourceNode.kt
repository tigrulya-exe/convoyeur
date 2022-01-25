package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.ConsumerGraphNode
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.api.declaration.StatefulGraphNode
import ru.nsu.convoyeur.api.execution.context.SourceExecutionContext
import java.util.*

open class SourceNode<V>(
    override val id: String = GraphNodeIdProvider.provideId(),
    override var parallelism: Int = 1,
    override var outputNodes: MutableList<ConsumerGraphNode<V, *>> = mutableListOf(),
    override val action: suspend SourceExecutionContext<V>.() -> Unit,
) : SourceGraphNode<V>, StatefulGraphNode<Nothing, V, SourceExecutionContext<V>>

suspend fun <V> SourceExecutionContext<V>.emit(nodeId: String, value: V) {
    outputChannel(nodeId)?.send(value)
}

/**
 * Emits to first output channel
 */
suspend fun <V> SourceExecutionContext<V>.emit(value: V) {
    outputChannels().values.firstOrNull()?.send(value)
}

fun <V> Iterable<V>.asSourceNode(
    id: String = UUID.randomUUID().toString(),
    outputChannelName: String? = null
): SourceGraphNode<V> {
    return SourceNode(
        id = id,
        action = {
            this@asSourceNode.forEach {
                outputChannelName?.let { name ->
                    emit(name, it)
                } ?: emit(it)
            }
        }
    )
}
