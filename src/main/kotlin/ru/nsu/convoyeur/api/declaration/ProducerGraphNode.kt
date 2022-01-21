package ru.nsu.convoyeur.api.declaration

interface ProducerGraphNode<S, D>: GraphNode<S, D> {
    fun to(outNode: SinkGraphNode<D>) : SinkGraphNode<D> {
        outputNodes.add(outNode)
        return outNode
    }

    fun <OUT> via(outNode: TransformGraphNode<D, OUT>) : TransformGraphNode<D, OUT> {
        outputNodes.add(outNode)
        return outNode
    }
}

interface SourceGraphNode<V> : ProducerGraphNode<Nothing, V> {
    fun goesTo(vararg outNodes: SinkGraphNode<V>) : SourceGraphNode<V> {
        outputNodes.addAll(outNodes.asList())
        return this
    }

    fun <OUT> goesVia(vararg outNodes: TransformGraphNode<V, OUT>) : SourceGraphNode<V> {
        outputNodes.addAll(outNodes.asList())
        return this
    }
}

interface TransformGraphNode<S, D> : ConsumerGraphNode<S, D>, ProducerGraphNode<S, D> {
    fun goesTo(vararg outNodes: SinkGraphNode<D>) : TransformGraphNode<S, D> {
        outputNodes.addAll(outNodes.asList())
        return this
    }

    fun <OUT> goesVia(vararg outNodes: TransformGraphNode<D, OUT>) : TransformGraphNode<S, D> {
        outputNodes.addAll(outNodes.asList())
        return this
    }
}
