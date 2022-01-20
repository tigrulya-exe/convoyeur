package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.ConsumerGraphNode
import ru.nsu.convoyeur.api.declaration.StatefulGraphNode
import ru.nsu.convoyeur.api.declaration.StatelessConsumerNode
import ru.nsu.convoyeur.api.execution.context.NodeExecutionContext

class StatefulTransformNode<S, D>(
    override val id: String = GraphNodeIdProvider.provideId(),
    override val bufferSizes: Map<String, Int> = mutableMapOf(),
    override var outputNodes: List<ConsumerGraphNode<D, *>> = listOf(),
    override val action: suspend NodeExecutionContext<S, D>.() -> Unit,
) : ConsumerGraphNode<S, D>, StatefulGraphNode<S, D, NodeExecutionContext<S, D>>

class TransformNode<S, D>(
    override val id: String = GraphNodeIdProvider.provideId(),
    override val onChannelClose: suspend NodeExecutionContext<S, D>.(String) -> Unit = {},
    override val bufferSizes: Map<String, Int> = mutableMapOf(),
    override var outputNodes: List<ConsumerGraphNode<D, *>> = listOf(),
    override val action: suspend NodeExecutionContext<S, D>.(String, S) -> Unit,
) : StatelessConsumerNode<S, D, NodeExecutionContext<S, D>>