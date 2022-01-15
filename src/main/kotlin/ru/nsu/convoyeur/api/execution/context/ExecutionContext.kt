package ru.nsu.convoyeur.api.execution.context

import kotlinx.coroutines.channels.Channel

// иммутабельные интерфесы предоставляются юзеру
// для использования в выполняемых на нодах функциях
interface SourceExecutionContext<V> {
    fun outputChannel(nodeId: String): Channel<V>?
}

interface ConsumerExecutionContext<V> {
    fun inputChannel(nodeId: String): Channel<V>?
}

interface ExecutionContext<S, D>: SourceExecutionContext<D>, ConsumerExecutionContext<S>

interface MutableSourceExecutionContext<V> : SourceExecutionContext<V> {
    var outputChannels: MutableMap<String, Channel<V>>

    fun addOutputChannel(nodeId: String, channel: Channel<V>) {
        outputChannels[nodeId] = channel
    }

    override fun outputChannel(nodeId: String) = outputChannels[nodeId]
}

interface MutableConsumerExecutionContext<V> : ConsumerExecutionContext<V> {
    var inputChannels: MutableMap<String, Channel<V>>

    fun addInputChannel(nodeId: String, channel: Channel<V>) {
        inputChannels[nodeId] = channel
    }

    override fun inputChannel(nodeId: String) = inputChannels[nodeId]
}

interface MutableExecutionContext<S, D> : MutableSourceExecutionContext<D>,
    MutableConsumerExecutionContext<S>,
    ExecutionContext<S, D>