package ru.nsu.convoyeur.api.declaration

import ru.nsu.convoyeur.api.execution.context.ConsumerExecutionContext

open class SinkNode<V>(
    override val id: String,
    val consumer: suspend ConsumerExecutionContext<V>.() -> Unit,
) : SinkGraphNode<V>
