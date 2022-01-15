package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.SinkGraphNode
import ru.nsu.convoyeur.api.execution.context.ConsumerExecutionContext

open class SinkNode<V>(
    override val id: String,
    override val bufferSize: Int = 1,
    val consumer: suspend ConsumerExecutionContext<V>.() -> Unit,
) : SinkGraphNode<V>
