package ru.nsu.convoyeur.core.execution.graph

import ru.nsu.convoyeur.api.channel.ChannelFactory
import ru.nsu.convoyeur.api.declaration.graph.GraphNode
import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphNodeBuilder
import ru.nsu.convoyeur.core.channel.CoroutineChannelFactory
import ru.nsu.convoyeur.core.execution.context.NodeExecutionContext
import ru.nsu.convoyeur.util.pop
import ru.nsu.convoyeur.util.withDefaultCompute

class SimpleBFSExecutionGraphBuilder(
    channelFactory: ChannelFactory = CoroutineChannelFactory(),
    nodesTransformer: ExecutionGraphNodeBuilder = DefaultExecutionGraphNodeBuilder()
) : AbstractExecutionGraphBuilder(channelFactory, nodesTransformer) {

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
    override fun traverseGraph(sources: List<SourceGraphNode<*>>): Map<String, ExecutionGraphNode<*, *>> {
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

            traverseQueue.addAll(
                node.outputNodes
                    .filter { it.id !in traversedNodes }
            )

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
}