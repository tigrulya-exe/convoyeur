package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.SinkGraphNode
import ru.nsu.convoyeur.api.execution.context.ConsumerExecutionContext

open class StatefulSinkNode<V>(
    override val id: String,
    override val bufferSizes: Map<String, Int> = mutableMapOf(),
    val consumer: suspend ConsumerExecutionContext<V>.() -> Unit,
) : SinkGraphNode<V>

open class SinkNode<V>(
    override val id: String,
    override val bufferSizes: Map<String, Int> = mutableMapOf(),
    val callback: suspend ConsumerExecutionContext<V>.(String, V?) -> Unit,
) : SinkGraphNode<V>