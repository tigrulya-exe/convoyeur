package ru.nsu.convoyeur.api.declaration.graph

interface ProducerGraphNode<S, D>: GraphNode<S, D> {
    fun to(outNode: SinkGraphNode<D>, bufferSize: Int = 1) : SinkGraphNode<D> {
        outputNodes.add(outNode)
        outNode.bufferSizes[id] = bufferSize
        return outNode
    }

    fun <OUT> via(outNode: TransformGraphNode<D, OUT>, bufferSize: Int = 1) : TransformGraphNode<D, OUT> {
        outputNodes.add(outNode)
        outNode.bufferSizes[id] = bufferSize
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

    fun goesTo(outNode: SinkGraphNode<V>, bufferSize: Int = 1) : SourceGraphNode<V> {
        to(outNode, bufferSize)
        return this
    }

    fun <OUT> goesVia(outNode: TransformGraphNode<V, OUT>, bufferSize: Int = 1) : SourceGraphNode<V> {
        via(outNode, bufferSize)
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

    fun goesTo(outNode: SinkGraphNode<D>, bufferSize: Int = 1) : TransformGraphNode<S, D> {
        to(outNode, bufferSize)
        return this
    }

    fun <OUT> goesVia(outNode: TransformGraphNode<D, OUT>, bufferSize: Int = 1) : TransformGraphNode<S, D> {
        via(outNode, bufferSize)
        return this
    }
}
