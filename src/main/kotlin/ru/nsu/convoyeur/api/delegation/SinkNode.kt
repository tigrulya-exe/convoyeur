package ru.nsu.convoyeur.api.delegation

import ru.nsu.convoyeur.api.channel.key.ChannelKey
import ru.nsu.convoyeur.api.execution.context.SinkExecutionContext

open class SinkNode<V>(
    val action: suspend SinkExecutionContext<V>.() -> Unit,
    //todo TMP
    var inputNodes: Map<ChannelKey, ProducerNode<V>> = mapOf(),
) : ConsumerNode<V>
