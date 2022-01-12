package ru.nsu.convoyeur.api.execution.context

import ru.nsu.convoyeur.api.channel.DataChannel

interface ExecutionContext<S, D> : SourceExecutionContext<D>, ConsumerExecutionContext<S>

class NodeExecutionContext<S, D>(
    inputChannels: MutableMap<String, DataChannel<S>> = mutableMapOf(),
    outputChannels: MutableMap<String, DataChannel<D>> = mutableMapOf(),
) : ExecutionContext<S, D> {

    private val sourceContext = DefaultSourceExecutionContext(outputChannels)
    private val sinkContext = DefaultSinkExecutionContext(inputChannels)

    override fun inputChannel(nodeId: String) = sinkContext.inputChannel(nodeId)

    override fun outputChannel(nodeId: String) = sourceContext.outputChannel(nodeId)

    override fun addInputChannel(nodeId: String, channel: DataChannel<S>) =
        sinkContext.addInputChannel(nodeId, channel)

    override fun addOutputChannel(nodeId: String, channel: DataChannel<D>) =
        sourceContext.addOutputChannel(nodeId, channel)

    override val inputChannels: Map<String, DataChannel<S>>
        get() = sinkContext.inputChannels

}