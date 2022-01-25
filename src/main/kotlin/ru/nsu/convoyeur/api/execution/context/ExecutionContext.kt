package ru.nsu.convoyeur.api.execution.context

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

// иммутабельные интерфесы предоставляются юзеру
// для использования в выполняемых на нодах функциях
interface ExecutionContext {
    val isActive: Boolean
    val parallelIndex: Int // индекс параллельного экземпляра узла (от 0 до parallelism - 1)
}

interface SourceExecutionContext<V> : ExecutionContext {
    fun outputChannels(): Map<String, SendChannel<V>>

    fun outputChannel(nodeId: String) = outputChannels()[nodeId]
}

interface ConsumerExecutionContext<out V> : ExecutionContext {
    fun inputChannels(): Map<String, ReceiveChannel<V>>

    fun inputChannel(nodeId: String) = inputChannels()[nodeId]

    fun inputChannel() = inputChannels().values.firstOrNull()

    val hasOpenInputChannels: Boolean
        get() = inputChannels().values
            .map { !it.isClosedForReceive }
            .reduce(Boolean::or)
}

interface NodeExecutionContext<S, D> : SourceExecutionContext<D>, ConsumerExecutionContext<S>

