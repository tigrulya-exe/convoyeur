package ru.nsu.convoyeur.api.execution.context

import ru.nsu.convoyeur.api.channel.DataChannel

interface SourceExecutionContext<V> {
    suspend fun emit(nodeId: String, value: V)
}

class DefaultSourceExecutionContext<V>(
    private val outputChannels: Map<String, DataChannel<V>>
) : SourceExecutionContext<V> {

    override suspend fun emit(nodeId: String, value: V) {
        outputChannels[nodeId]?.put(value);
    }
}