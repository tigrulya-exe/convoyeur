package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.graph.ConsumerGraphNode
import ru.nsu.convoyeur.api.declaration.graph.StatefulGraphNode
import ru.nsu.convoyeur.api.declaration.graph.StatelessConsumerNode
import ru.nsu.convoyeur.api.declaration.graph.TransformGraphNode
import ru.nsu.convoyeur.api.execution.context.NodeExecutionContext

class StatefulTransformNode<S, D>(
    override val id: String = GraphNodeIdProvider.provideId(),
    override var parallelism: Int = 1,
    override val bufferSizes: MutableMap<String, Int> = mutableMapOf(),
    override var outputNodes: MutableList<ConsumerGraphNode<D, *>> = mutableListOf(),
    override val action: suspend NodeExecutionContext<S, D>.() -> Unit,
) : TransformGraphNode<S, D>, StatefulGraphNode<S, D, NodeExecutionContext<S, D>>

class TransformNode<S, D>(
    override val id: String = GraphNodeIdProvider.provideId(),
    override var parallelism: Int = 1,
    override val onChannelClose: suspend NodeExecutionContext<S, D>.(String) -> Unit = {},
    override val bufferSizes: MutableMap<String, Int> = mutableMapOf(),
    override var outputNodes: MutableList<ConsumerGraphNode<D, *>> = mutableListOf(),
    override val action: suspend NodeExecutionContext<S, D>.(String, S) -> Unit,
) : TransformGraphNode<S, D>, StatelessConsumerNode<S, D, NodeExecutionContext<S, D>>