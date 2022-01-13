package ru.nsu.convoyeur.api.execution.context

import ru.nsu.convoyeur.api.channel.DataChannel

// иммутабельные интерфесы предоставляются юзеру
// для использования в выполняемых на нодах функциях
interface SourceExecutionContext<V> {
    fun outputChannel(nodeId: String): DataChannel<V>?
}

interface ConsumerExecutionContext<V> {
    fun inputChannel(nodeId: String): DataChannel<V>?
}

interface ExecutionContext<S, D>: SourceExecutionContext<D>, ConsumerExecutionContext<S>

interface MutableSourceExecutionContext<V> : SourceExecutionContext<V> {
    var outputChannels: MutableMap<String, DataChannel<V>>

    fun addOutputChannel(nodeId: String, channel: DataChannel<V>) {
        outputChannels[nodeId] = channel
    }

    override fun outputChannel(nodeId: String) = outputChannels[nodeId]
}

interface MutableConsumerExecutionContext<V> : ConsumerExecutionContext<V> {
    var inputChannels: MutableMap<String, DataChannel<V>>

    fun addInputChannel(nodeId: String, channel: DataChannel<V>) {
        inputChannels[nodeId] = channel
    }

    override fun inputChannel(nodeId: String) = inputChannels[nodeId]
}

interface MutableExecutionContext<S, D> : MutableSourceExecutionContext<D>,
    MutableConsumerExecutionContext<S>,
    ExecutionContext<S, D>