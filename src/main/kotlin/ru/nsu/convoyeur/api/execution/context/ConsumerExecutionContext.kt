package ru.nsu.convoyeur.api.execution.context

import ru.nsu.convoyeur.api.channel.DataChannel

interface ConsumerExecutionContext<V> {
    //todo TMP
    val inputChannels: Map<String, DataChannel<V>>

    fun inputChannel(nodeId: String): DataChannel<V>?

    fun addInputChannel(nodeId: String, channel: DataChannel<V>)
}

class DefaultSinkExecutionContext<V>(
    override val inputChannels: MutableMap<String, DataChannel<V>>
) : ConsumerExecutionContext<V> {

    override fun inputChannel(nodeId: String): DataChannel<V>? {
        return inputChannels[nodeId]
    }

    override fun addInputChannel(nodeId: String, channel: DataChannel<V>) {
        inputChannels[nodeId] = channel
    }
}