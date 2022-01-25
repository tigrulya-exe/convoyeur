package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.graph.SinkGraphNode
import ru.nsu.convoyeur.api.declaration.graph.StatefulGraphNode
import ru.nsu.convoyeur.api.declaration.graph.StatelessConsumerNode
import ru.nsu.convoyeur.api.execution.context.ConsumerExecutionContext

open class StatefulSinkNode<V>(
    override val id: String = GraphNodeIdProvider.provideId(),
    override var parallelism: Int = 1,
    override val bufferSizes: MutableMap<String, Int> = mutableMapOf(),
    override val action: suspend ConsumerExecutionContext<V>.() -> Unit,
) : SinkGraphNode<V>, StatefulGraphNode<V, Nothing, ConsumerExecutionContext<V>>

open class SinkNode<V>(
    override val id: String = GraphNodeIdProvider.provideId(),
    override var parallelism: Int = 1,
    override val bufferSizes: MutableMap<String, Int> = mutableMapOf(),
    override val onChannelClose: suspend ConsumerExecutionContext<V>.(String) -> Unit = {},
    override val action: suspend ConsumerExecutionContext<V>.(String, V) -> Unit,
) : SinkGraphNode<V>, StatelessConsumerNode<V, Nothing, ConsumerExecutionContext<V>>