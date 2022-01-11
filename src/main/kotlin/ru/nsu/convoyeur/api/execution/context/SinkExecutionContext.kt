package ru.nsu.convoyeur.api.execution.context

import ru.nsu.convoyeur.api.channel.DataChannel

interface SinkExecutionContext<V> {
    fun inputChannel(nodeId: String): DataChannel<V>?
}

class DefaultSinkExecutionContext<V>(
    private val inputChannels: Map<String, DataChannel<V>>
) : SinkExecutionContext<V> {

    override fun inputChannel(nodeId: String): DataChannel<V>? {
        return inputChannels[nodeId]
    }
}