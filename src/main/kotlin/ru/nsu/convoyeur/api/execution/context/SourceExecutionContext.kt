package ru.nsu.convoyeur.api.execution.context

import ru.nsu.convoyeur.api.channel.DataChannel

interface SourceExecutionContext<V> {
    fun outputChannel(nodeId: String): DataChannel<V>?

    fun addOutputChannel(nodeId: String, channel: DataChannel<V>)
}

class DefaultSourceExecutionContext<V>(
    private val outputChannels: MutableMap<String, DataChannel<V>>
) : SourceExecutionContext<V> {
    override fun outputChannel(nodeId: String) = outputChannels[nodeId]

    override fun addOutputChannel(nodeId: String, channel: DataChannel<V>) {
        outputChannels[nodeId] = channel
    }
}