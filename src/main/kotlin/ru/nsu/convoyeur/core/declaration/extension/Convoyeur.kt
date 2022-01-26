package ru.nsu.convoyeur.core.declaration.extension

import ru.nsu.convoyeur.api.declaration.graph.ConsumerGraphNode
import ru.nsu.convoyeur.api.declaration.graph.GraphNode
import ru.nsu.convoyeur.api.declaration.graph.ProducerGraphNode
import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.api.execution.context.NodeExecutionContext
import ru.nsu.convoyeur.core.declaration.graph.*
import ru.nsu.convoyeur.core.execution.DefaultExecutionManager
import ru.nsu.convoyeur.util.getFirstChannelValue
import java.util.stream.Collector

class Convoyeur<S, D, SS>(
    private val sourceNode: SourceGraphNode<SS>,
    private val currentNode: GraphNode<S, D>
) {
    fun <V> map(action: (D) -> V) = constructTransformNode<V> {
        emit(action(it))
    }

    fun filter(predicate: (D) -> Boolean) = constructTransformNode<D> {
        if (predicate(it)) {
            emit(it)
        }
    }

    fun peek(action: (D) -> Unit) = constructTransformNode<D> {
        action(it)
        emit(it)
    }

    fun collect(collector: (D) -> Unit) {
        val sinkNode = SinkNode<D> { _, value -> collector(value) }
        addNode(currentNode as ProducerGraphNode<S, D>, sinkNode)
        DefaultExecutionManager().execute(listOf(sourceNode))
    }

    fun <R, A> collect(collector: Collector<in D, A, R>): R {
        val result = collector.supplier().get()
        val sinkNode = StatefulSinkNode<D> {
            val inputChannels = this.inputChannels()
            val openChannelKeys = HashSet(inputChannels.keys)

            while (isActive && openChannelKeys.isNotEmpty()) {
                val channelNameValue = getFirstChannelValue(
                    inputChannels.filter { it.key in openChannelKeys }
                )
                if (channelNameValue.second == null) {
                    openChannelKeys.remove(channelNameValue.first)
                    continue
                }

                collector.accumulator().accept(result, channelNameValue.second)
            }
        }
        addNode(currentNode as ProducerGraphNode<S, D>, sinkNode)
        DefaultExecutionManager().execute(listOf(sourceNode))
        return collector.finisher().apply(result)
    }

    private fun <V> constructTransformNode(
        action: suspend NodeExecutionContext<D, V>.(D) -> Unit
    ): Convoyeur<D, V, SS> {
        val transformNode = TransformNode<D, V> { _, value -> action(value) }
        addNode(currentNode as ProducerGraphNode<S, D>, transformNode)
        return Convoyeur(sourceNode, transformNode)
    }

    private fun <S, D> addNode(source: ProducerGraphNode<S, D>, destination: ConsumerGraphNode<D, *>) {
        source.outputNodes.add(destination)
    }
}

fun <V> Iterable<V>.convoyeur(): Convoyeur<Nothing, V, V> = this.asSourceNode().let {
    Convoyeur(it, it)
}
