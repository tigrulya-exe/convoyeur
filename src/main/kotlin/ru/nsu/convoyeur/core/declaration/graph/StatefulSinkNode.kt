package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.SinkGraphNode
import ru.nsu.convoyeur.api.declaration.StatefulGraphNode
import ru.nsu.convoyeur.api.declaration.StatelessConsumerNode
import ru.nsu.convoyeur.api.execution.context.ConsumerExecutionContext

open class StatefulSinkNode<V>(
    override val id: String,
    override val bufferSizes: Map<String, Int> = mutableMapOf(),
    override val action: suspend ConsumerExecutionContext<V>.() -> Unit,
) : SinkGraphNode<V>, StatefulGraphNode<V, Nothing, ConsumerExecutionContext<V>>

open class SinkNode<V>(
    override val id: String,
    override val bufferSizes: Map<String, Int> = mutableMapOf(),
    override val action: suspend ConsumerExecutionContext<V>.(String, V) -> Unit,
    override val onChannelClose: suspend ConsumerExecutionContext<V>.(String) -> Unit = {},
) : SinkGraphNode<V>, StatelessConsumerNode<V, Nothing, ConsumerExecutionContext<V>>