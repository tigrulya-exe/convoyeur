package ru.nsu.convoyeur.api.declaration

interface Node {
    val id: String
}

interface ProducerNode<V> : Node {
    var outputNodes: List<ConsumerNode<V>>
}

interface ConsumerNode<V> : Node