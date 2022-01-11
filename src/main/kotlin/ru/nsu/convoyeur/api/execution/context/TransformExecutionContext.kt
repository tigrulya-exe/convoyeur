package ru.nsu.convoyeur.api.execution.context

import ru.nsu.convoyeur.api.channel.DataChannel

interface TransformExecutionContext<S, D> : SourceExecutionContext<D>, SinkExecutionContext<S>

class DefaultTransformExecutionContext<S, D>(
    inputChannels: Map<String, DataChannel<S>>,
    outputChannels: Map<String, DataChannel<D>>
) : TransformExecutionContext<S, D> {

    private val sourceContext = DefaultSourceExecutionContext(outputChannels)
    private val sinkExecutionContext = DefaultSinkExecutionContext(inputChannels)

    override fun inputChannel(nodeId: String) = sinkExecutionContext.inputChannel(nodeId)

    override suspend fun emit(nodeId: String, value: D) = sourceContext.emit(nodeId, value)
}