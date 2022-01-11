package ru.nsu.convoyeur.api.declaration

import ru.nsu.convoyeur.api.execution.context.SinkExecutionContext

open class SinkNode<V>(
    override val id: String,
    val consumer: suspend SinkExecutionContext<V>.() -> Unit,
) : ConsumerNode<V>
