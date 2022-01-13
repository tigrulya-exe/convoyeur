package ru.nsu.convoyeur.core.execution.graph

import ru.nsu.convoyeur.api.channel.DataChannel
import ru.nsu.convoyeur.api.channel.DataChannelFactory
import ru.nsu.convoyeur.api.declaration.GraphNode
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.GraphNodeTransformer
import ru.nsu.convoyeur.api.execution.graph.transform.GraphTransformer
import ru.nsu.convoyeur.core.channel.CoroutineDataChannelFactory
import ru.nsu.convoyeur.core.declaration.graph.SourceNode
import ru.nsu.convoyeur.core.execution.context.NodeExecutionContext
import ru.nsu.convoyeur.util.pop
import ru.nsu.convoyeur.util.withDefaultCompute

@Suppress("UNCHECKED_CAST")
class DefaultGraphTransformer(
    private val channelFactory: DataChannelFactory = CoroutineDataChannelFactory(),
    private val nodesTransformer: GraphNodeTransformer = DefaultGraphNodeTransformer()
) : GraphTransformer {

    override fun <V> transform(source: SourceNode<V>) = transform(listOf(source))[0]

    override fun <V> transform(sources: List<SourceNode<V>>): List<ExecutionGraphNode<Nothing, V>> {
        val executionNodes = traverseGraph(sources)
        return sources.map { it.id }
            .map { executionNodes[it] }
            .map { it as ExecutionGraphNode<Nothing, V> }
    }

    private fun traverseGraph(sources: List<SourceNode<*>>): Map<String, ExecutionGraphNode<*, *>> {
        // преобразованные ноды
        val traversedNodes = mutableMapOf<String, ExecutionGraphNode<*, *>>()
        // выделены в отдельную мапу, т.к. родители создают контексты для еще не созданных детей
        val executionContexts = mutableMapOf<String, MutableExecutionContext<*, *>>()
            .withDefaultCompute { NodeExecutionContext<Any, Any>() }
        // очередь обхода
        val traverseQueue = LinkedHashSet<GraphNode<*, *>>(sources)

        while (traverseQueue.isNotEmpty()) {
            println(traverseQueue.map { node -> node.id })
            val node = traverseQueue.pop()
            traverseQueue.addAll(node.outputNodes)
            traversedNodes[node.id] = transformNode(node, executionContexts).also {
                // добавляем у родителя ссылку на только что трансформированную ноду
                addChildLinkToParent(
                    it,
                    executionContexts[it.id],
                    traversedNodes
                )
            }
        }

        return traversedNodes;
    }

    private fun <S, D> transformNode(
        node: GraphNode<S, D>,
        executionContexts: MutableMap<String, MutableExecutionContext<*, *>>
    ): ExecutionGraphNode<out S, out D> {
        val outputChannels = mutableMapOf<String, DataChannel<D>>()

        for (child in node.outputNodes) {
            val childContext = executionContexts[child.id] as NodeExecutionContext<D, Any>
            channelFactory.createChannel<D>().apply {
                outputChannels[child.id] = this
                childContext.addInputChannel(node.id, this)
            }
        }

        val context = executionContexts[node.id] as NodeExecutionContext<S, D>
        return nodesTransformer.transform(node, context.also { it.outputChannels = outputChannels })
    }

    private fun <S, D> addChildLinkToParent(
        child: ExecutionGraphNode<S, D>,
        context: MutableExecutionContext<*, *>?,
        nodes: Map<String, ExecutionGraphNode<*, *>>
    ) {
        context?.let {
            context.inputChannels
                .keys
                .mapNotNull {
                    nodes[it] as? ExecutionGraphNode<*, S>
                }
                .forEach {
                    it.neighbours[child.id] = child
                }
        }
    }


}