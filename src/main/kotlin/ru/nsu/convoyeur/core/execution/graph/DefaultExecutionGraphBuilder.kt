package ru.nsu.convoyeur.core.execution.graph

import kotlinx.coroutines.channels.Channel
import ru.nsu.convoyeur.api.channel.ChannelFactory
import ru.nsu.convoyeur.api.declaration.GraphNode
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphBuilder
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphNodeBuilder
import ru.nsu.convoyeur.core.channel.CoroutineChannelFactory
import ru.nsu.convoyeur.core.execution.context.NodeExecutionContext
import ru.nsu.convoyeur.util.pop
import ru.nsu.convoyeur.util.withDefaultCompute

@Suppress("UNCHECKED_CAST")
class DefaultExecutionGraphBuilder(
    private val channelFactory: ChannelFactory = CoroutineChannelFactory(),
    private val nodesTransformer: ExecutionGraphNodeBuilder = DefaultExecutionGraphNodeBuilder()
) : ExecutionGraphBuilder {

    override fun <V> build(source: SourceGraphNode<V>) = build(listOf(source))

    override fun <V> build(sources: List<SourceGraphNode<V>>): ExecutionGraph {
        return ExecutionGraph(
            nodes = traverseGraph(sources),
            sourceIds = sources.map { it.id }
        )
    }

    /**
     * Как обходим граф:
     * Это алгоритм обхода в ширину ориентированного графа, начиная с вершин, указанных пользователем.
     * Обработка вершины == трансформация DeclarationGraphNode<S, V> в ExecutionGraphNode<S, V>:
     *  1. Создаем выходные каналы для данной ноды и добавляем их как outputChannels в ExecutionContext той же ноды
     *  2. Каждый такой выходной канал добавляем как inputChannel контекста соответствующей дочерней ноды
     *     (который достаем из executionContexts) и обновляем данный контекст в executionContexts
     *  3. Обогащаем определенную пользователем функцию DeclarationGraphNode ExecutionContextом обрабатываемой ноды
     *     и создаем экземпляр ExecutionGraphNode
     *  4. В случае если у обрабатываемой ноды есть родительские узлы, то добавляем ее в ExecutionContext.children родителя
     *
     */
    private fun traverseGraph(sources: List<SourceGraphNode<*>>): Map<String, ExecutionGraphNode<*, *>> {
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

        return traversedNodes
    }

    private fun <S, D> transformNode(
        node: GraphNode<S, D>,
        executionContexts: MutableMap<String, MutableExecutionContext<*, *>>
    ): ExecutionGraphNode<out S, out D> {
        val outputChannels = mutableMapOf<String, Channel<D>>()

        for (child in node.outputNodes) {
            val childContext = executionContexts[child.id] as NodeExecutionContext<D, Any>
            val channel = channelFactory.createChannel<D>(child.bufferSize)
            outputChannels[child.id] = channel
            childContext.addInputChannel(node.id, channel)
        }

        val context = executionContexts[node.id] as NodeExecutionContext<S, D>
        return nodesTransformer.build(node, context.also { it.outputChannels = outputChannels })
    }

    private fun <S, D> addChildLinkToParent(
        child: ExecutionGraphNode<S, D>,
        context: MutableExecutionContext<*, *>?,
        nodes: Map<String, ExecutionGraphNode<*, *>>
    ) {
        context?.let {
            context.inputChannels
                .keys
                .mapNotNull { nodes[it] as? ExecutionGraphNode<*, S> }
                .forEach { it.children[child.id] = child }
        }
    }


}