package ru.nsu.convoyeur.api.execution.context

import kotlinx.coroutines.channels.Channel

interface MutableNodeExecutionContext {
    var isActive: Boolean
}

interface MutableSourceExecutionContext<V> : SourceExecutionContext<V>, MutableNodeExecutionContext {
    var outputChannels: MutableMap<String, Channel<V>>

    fun addOutputChannel(nodeId: String, channel: Channel<V>) {
        outputChannels[nodeId] = channel
    }

    override fun outputChannels(): Map<String, Channel<V>> = outputChannels
}

interface MutableConsumerExecutionContext<V> : ConsumerExecutionContext<V>, MutableNodeExecutionContext {
    var inputChannels: MutableMap<String, Channel<V>>

    fun addInputChannel(nodeId: String, channel: Channel<V>) {
        inputChannels[nodeId] = channel
    }

    override fun inputChannels(): Map<String, Channel<V>> = inputChannels
}

interface MutableExecutionContext<S, D> : MutableSourceExecutionContext<D>,
    MutableConsumerExecutionContext<S>,
    NodeExecutionContext<S, D>