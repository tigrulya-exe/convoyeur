package ru.nsu.convoyeur.core.declaration.graph

import ru.nsu.convoyeur.api.declaration.GraphNode
import ru.nsu.convoyeur.api.execution.context.ExecutionContext

class TransformNode<S, D>(
    override val id: String,
    val transform: suspend ExecutionContext<S, D>.() -> Unit,
    override var outputNodes: List<GraphNode<D, *>> = listOf(),
) : GraphNode<S, D>