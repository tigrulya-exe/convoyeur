package ru.nsu.convoyeur.api.declaration

interface GraphNode<S, D> {
    val id: String
    var outputNodes: List<ConsumerNode<D, *>>
}

interface ConsumerNode<S, D> : GraphNode<S, D> {
    val bufferSize: Int
}

interface SourceGraphNode<V> : GraphNode<Nothing, V>

interface SinkGraphNode<V> : ConsumerNode<V, Nothing> {
    override var outputNodes: List<ConsumerNode<Nothing, *>>
        get() = emptyList()
        set(_) {}
}