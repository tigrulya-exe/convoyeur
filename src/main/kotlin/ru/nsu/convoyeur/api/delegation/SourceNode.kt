package ru.nsu.convoyeur.api.delegation

import ru.nsu.convoyeur.api.channel.key.ChannelKey
import ru.nsu.convoyeur.api.execution.context.SourceExecutionContext

open class SourceNode<V>(
    val producer: suspend SourceExecutionContext<V>.() -> Unit,
    var outputNodes: Map<ChannelKey, ConsumerNode<V>> = mapOf(),
) : ProducerNode<V>
