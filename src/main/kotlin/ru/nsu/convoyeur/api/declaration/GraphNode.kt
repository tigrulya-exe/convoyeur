package ru.nsu.convoyeur.api.declaration

import ru.nsu.convoyeur.api.execution.context.ExecutionContext

interface GraphNode<S, D> {
    val id: String
    var outputNodes: MutableList<ConsumerGraphNode<D, *>>
}

interface StatefulGraphNode<S, D, C : ExecutionContext> : GraphNode<S, D> {
    val action: suspend C.() -> Unit
}
