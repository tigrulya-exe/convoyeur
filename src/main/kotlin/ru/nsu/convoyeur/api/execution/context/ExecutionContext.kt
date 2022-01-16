package ru.nsu.convoyeur.api.execution.context

import kotlinx.coroutines.channels.Channel

// иммутабельные интерфесы предоставляются юзеру
// для использования в выполняемых на нодах функциях
interface ExecutionContext {
    val isActive: Boolean
}

interface SourceExecutionContext<V> : ExecutionContext {
    fun outputChannels(): Map<String, Channel<V>>

    fun outputChannel(nodeId: String) = outputChannels()[nodeId]
}

interface ConsumerExecutionContext<V> : ExecutionContext {
    fun inputChannels(): Map<String, Channel<V>>

    fun inputChannel(nodeId: String) = inputChannels()[nodeId]

    val hasOpenInputChannels: Boolean
        get() = inputChannels().values
            .map { !it.isClosedForReceive }
            .reduce(Boolean::or)
}

interface NodeExecutionContext<S, D> : SourceExecutionContext<D>, ConsumerExecutionContext<S>

