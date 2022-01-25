package ru.nsu.convoyeur.api.declaration.graph

import ru.nsu.convoyeur.api.execution.context.ConsumerExecutionContext

interface ConsumerGraphNode<S, D> : GraphNode<S, D> {
    val bufferSizes: MutableMap<String, Int>
}

interface StatelessConsumerNode<S, D, in C : ConsumerExecutionContext<S>> : ConsumerGraphNode<S, D> {
    val action: suspend C.(String, S) -> Unit
    val onChannelClose: suspend C.(String) -> Unit
}

interface SinkGraphNode<V> : ConsumerGraphNode<V, Nothing> {
    override var outputNodes: MutableList<ConsumerGraphNode<Nothing, *>>
        get() = ArrayList()
        set(_) {}
}
