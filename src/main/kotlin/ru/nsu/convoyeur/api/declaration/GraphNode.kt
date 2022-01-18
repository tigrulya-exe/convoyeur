package ru.nsu.convoyeur.api.declaration

import ru.nsu.convoyeur.api.execution.context.ExecutionContext

interface GraphNode<S, D> {
    val id: String
    var outputNodes: List<ConsumerGraphNode<D, *>>
}

interface StatefulGraphNode<S, D, C : ExecutionContext> : GraphNode<S, D> {
    val action: suspend C.() -> Unit
}
