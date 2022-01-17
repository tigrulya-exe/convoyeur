package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.ConsumerNode
import ru.nsu.convoyeur.api.execution.context.NodeExecutionContext

class StatefulTransformNode<S, D>(
    override val id: String,
    val transform: suspend NodeExecutionContext<S, D>.() -> Unit,
    override val bufferSizes: Map<String, Int> = mutableMapOf(),
    override var outputNodes: List<ConsumerNode<D, *>> = listOf(),
) : ConsumerNode<S, D>

class TransformNode<S, D>(
    override val id: String,
    val callback: suspend NodeExecutionContext<S, D>.(String, S?) -> Unit,
    override val bufferSizes: Map<String, Int> = mutableMapOf(),
    override var outputNodes: List<ConsumerNode<D, *>> = listOf(),
) : ConsumerNode<S, D>