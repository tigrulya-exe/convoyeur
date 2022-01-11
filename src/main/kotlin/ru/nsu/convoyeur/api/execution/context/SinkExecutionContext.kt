package ru.nsu.convoyeur.api.execution.context

import ru.nsu.convoyeur.api.channel.DataChannel
import ru.nsu.convoyeur.api.channel.key.ChannelKey

interface SinkExecutionContext<V> {
    fun inputChannel(key: ChannelKey): DataChannel<V>?
}

class DefaultSinkExecutionContext<V>(
    private val inputChannels: Map<ChannelKey, DataChannel<V>>
) : SinkExecutionContext<V> {

    override fun inputChannel(key: ChannelKey): DataChannel<V>? {
        return inputChannels[key]
    }
}