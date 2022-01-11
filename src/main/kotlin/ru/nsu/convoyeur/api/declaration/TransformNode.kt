package ru.nsu.convoyeur.api.declaration

import ru.nsu.convoyeur.api.execution.context.TransformExecutionContext

class TransformNode<S, D>(
    override val id: String,
    val transform: suspend TransformExecutionContext<S, D>.() -> Unit,
    override var outputNodes: List<ConsumerNode<D>> = listOf(),
) : ConsumerNode<S>, ProducerNode<D>