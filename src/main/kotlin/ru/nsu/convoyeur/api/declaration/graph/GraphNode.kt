package ru.nsu.convoyeur.api.declaration.graph

import ru.nsu.convoyeur.api.execution.context.ExecutionContext

interface GraphNode<S, D> {
    val id: String
    var outputNodes: MutableList<ConsumerGraphNode<D, *>>
    var parallelism: Int
}

interface StatefulGraphNode<S, D, C : ExecutionContext> : GraphNode<S, D> {
    val action: suspend C.() -> Unit
}
