package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.ConsumerNode
import ru.nsu.convoyeur.api.execution.context.ExecutionContext

class TransformNode<S, D>(
    override val id: String,
    val transform: suspend ExecutionContext<S, D>.() -> Unit,
    override val bufferSize: Int = 1,
    override var outputNodes: List<ConsumerNode<D, *>> = listOf(),
) : ConsumerNode<S, D>