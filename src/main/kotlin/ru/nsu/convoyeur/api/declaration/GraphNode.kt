package ru.nsu.convoyeur.api.declaration

interface GraphNode<S, D> {
    val id: String
    var outputNodes: List<GraphNode<D, *>>
}

interface SourceGraphNode<V> : GraphNode<Nothing, V>

interface SinkGraphNode<V> : GraphNode<V, Nothing> {
    override var outputNodes: List<GraphNode<Nothing, *>>
        get() = emptyList()
        set(_) {}
}