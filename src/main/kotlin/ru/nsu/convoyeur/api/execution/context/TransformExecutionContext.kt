package ru.nsu.convoyeur.api.execution.context

import ru.nsu.convoyeur.api.channel.DataChannel
import ru.nsu.convoyeur.api.channel.key.ChannelKey

interface TransformExecutionContext<S, D> : SourceExecutionContext<D>, SinkExecutionContext<S>

class DefaultTransformExecutionContext<S, D>(
    inputChannels: Map<ChannelKey, DataChannel<S>>,
    outputChannels: Map<ChannelKey, DataChannel<D>>
) : TransformExecutionContext<S, D> {

    private val sourceContext = DefaultSourceExecutionContext(outputChannels)
    private val sinkExecutionContext = DefaultSinkExecutionContext(inputChannels)

    override fun inputChannel(key: ChannelKey) = sinkExecutionContext.inputChannel(key)

    override suspend fun emit(key: ChannelKey, value: D) = sourceContext.emit(key, value)
}