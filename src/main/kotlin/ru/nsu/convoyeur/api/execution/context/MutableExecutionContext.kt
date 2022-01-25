package ru.nsu.convoyeur.api.execution.context

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

interface MutableNodeExecutionContext {
    var isActive: Boolean
    var parallelIndex: Int // индекс параллельного экземпляра узла (от 0 до parallelism - 1)
}

interface MutableSourceExecutionContext<V> : SourceExecutionContext<V>, MutableNodeExecutionContext {
    var outputChannels: MutableMap<String, SendChannel<V>>

    fun addOutputChannel(nodeId: String, channel: SendChannel<V>) {
        outputChannels[nodeId] = channel
    }

    override fun outputChannels(): Map<String, SendChannel<V>> = outputChannels
}

interface MutableConsumerExecutionContext<V> : ConsumerExecutionContext<V>, MutableNodeExecutionContext {
    var inputChannels: MutableMap<String, ReceiveChannel<V>>
    /**
     * Key - next cycle node id (output channel),
     * value - previous cycle node id (input channel)
     */
    var inputCycleChannelIds: MutableMap<String, String>

    fun addInputChannel(nodeId: String, channel: ReceiveChannel<V>) {
        inputChannels[nodeId] = channel
    }

    fun registerCycle(nextCycleNodeId: String, previousCycleNodeId: String) {
        inputCycleChannelIds[nextCycleNodeId] = previousCycleNodeId
    }

    override fun inputChannels(): Map<String, ReceiveChannel<V>> = inputChannels
}

interface MutableExecutionContext<S, D> : MutableSourceExecutionContext<D>,
    MutableConsumerExecutionContext<S>,
    NodeExecutionContext<S, D>