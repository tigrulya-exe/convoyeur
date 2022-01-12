package ru.nsu.convoyeur.api.execution.graph.transform

import ru.nsu.convoyeur.api.channel.DataChannel
import ru.nsu.convoyeur.api.declaration.GraphNode
import ru.nsu.convoyeur.api.declaration.SourceNode
import ru.nsu.convoyeur.api.execution.channel.DataChannelFactory
import ru.nsu.convoyeur.api.execution.context.ExecutionContext
import ru.nsu.convoyeur.api.execution.context.NodeExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ExecutionNode

class GraphTransformer(
    private val channelFactory: DataChannelFactory,
    private val nodesTransformer: NodeTransformer = NodeTransformer.create()
) {
    private val executionContexts = mutableMapOf<String, ExecutionContext<*, *>>()
    private val transformedNodes = mutableMapOf<String, ExecutionNode<*, *>>()

    fun <V> transform(source: SourceNode<V>): ExecutionNode<Nothing, V> {
        val transformQueue = LinkedHashSet<GraphNode<*, *>>().apply {
            add(source)
        }

        while (transformQueue.isNotEmpty()) {
            //TODO
            println(transformQueue.map{node -> node.id})
            val node = transformQueue.pop()
            // добавляем еще не встреченных детей в очередь обработки
            transformQueue.addAll(node.outputNodes)
            transformNode(node).let {
                // помечаем как транформированную
                transformedNodes[node.id] = it
                // добавляем у родителя ссылку на нас
                addChildToParentNeighbours(it)
            }
        }

        return transformedNodes[source.id] as ExecutionNode<Nothing, V>
    }

    private fun <E> LinkedHashSet<E>.pop(): E {
        val iterator = iterator()
        return iterator.next().also {
            iterator.remove()
        }
    }

    private fun <S, D> addChildToParentNeighbours(child: ExecutionNode<S, D>) {
        executionContexts[child.id]?.let { context ->
            context.inputChannels
                .keys
                .mapNotNull { transformedNodes[it] as? ExecutionNode<*, S> }
                .forEach {
                    it.neighbours[child.id] = child
                }
        }
    }

    private fun <S, D> transformNode(node: GraphNode<S, D>): ExecutionNode<out S, out D> {
        val outputChannels = mutableMapOf<String, DataChannel<D>>()

        for (child in node.outputNodes) {
            val channel = channelFactory.createChannel<D>()
            outputChannels[child.id] = channel
            executionContexts.computeIfAbsent(child.id) {
                NodeExecutionContext<D, Any>().apply {
                    addInputChannel(child.id, channel)
                }
            }
        }

        val context = executionContexts.computeIfAbsent(node.id) {
            NodeExecutionContext<S, D>(outputChannels = outputChannels)
        } as NodeExecutionContext<S, D>
        return nodesTransformer.transform(node, context)
    }
}