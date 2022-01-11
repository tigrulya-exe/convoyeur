package ru.nsu.convoyeur.api.delegation

import ru.nsu.convoyeur.api.channel.key.ChannelKey
import ru.nsu.convoyeur.api.execution.context.TransformExecutionContext

class TransformNode<S, D>(
    val action: suspend TransformExecutionContext<S, D>.() -> Unit,
    var outputNodes: Map<ChannelKey, ConsumerNode<D>> = mapOf(),
    // TODO
    var inputNodes: Map<ChannelKey, ProducerNode<S>> = mapOf()
) : ConsumerNode<S>, ProducerNode<D>