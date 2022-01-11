package ru.nsu.convoyeur.api.execution.context

import ru.nsu.convoyeur.api.channel.DataChannel
import ru.nsu.convoyeur.api.channel.key.ChannelKey

interface SourceExecutionContext<V> {
    suspend fun emit(key: ChannelKey, value: V)
}

class DefaultSourceExecutionContext<V>(
    private val outputChannels: Map<ChannelKey, DataChannel<V>>
) : SourceExecutionContext<V> {

    override suspend fun emit(key: ChannelKey, value: V) {
        outputChannels[key]?.put(value);
    }
}