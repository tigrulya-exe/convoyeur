package ru.nsu.convoyeur.api.declaration

import ru.nsu.convoyeur.api.execution.context.SourceExecutionContext

open class SourceNode<V>(
    override val id: String,
    val producer: suspend SourceExecutionContext<V>.() -> Unit,
    override var outputNodes: List<ConsumerNode<V>> = listOf(),
) : ProducerNode<V>
