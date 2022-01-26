package ru.nsu.convoyeur.core.execution.graph

import ru.nsu.convoyeur.api.channel.ChannelFactory
import ru.nsu.convoyeur.api.declaration.graph.GraphNode
import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphNodeBuilder
import ru.nsu.convoyeur.core.channel.CoroutineChannelFactory
import ru.nsu.convoyeur.core.execution.context.NodeExecutionContext
import ru.nsu.convoyeur.util.withDefaultCompute

enum class TraverseState {
    /**
     * Not traversed yet
     */
    NEW,
    /**
     * The node itself was traversed, but its children have not been yet
     */
    IN_PROGRESS,
    /**
     * Both node itself and its children were traversed
     */
    FINISHED
}

data class NodeMetaInfo(
    val node: ExecutionGraphNode<*, *>,
    var state: TraverseState = TraverseState.IN_PROGRESS,
    /**
     * Key - next cycle node id (output channel),
     * value - previous cycle node id (input channel)
     */
    var cycleNodeIds: Pair<String, String>? = null
)

class CycleDetectingExecutionGraphBuilder(
    channelFactory: ChannelFactory = CoroutineChannelFactory(),
    nodesTransformer: ExecutionGraphNodeBuilder = DefaultExecutionGraphNodeBuilder()
) : AbstractExecutionGraphBuilder(channelFactory, nodesTransformer) {

    override fun traverseGraph(sources: List<SourceGraphNode<*>>): Map<String, ExecutionGraphNode<*, *>> {
        // преобразованные ноды
        val traversedNodes = mutableMapOf<String, NodeMetaInfo>()
        // выделены в отдельную мапу, т.к. родители создают контексты для еще не созданных детей
        val executionContexts = mutableMapOf<String, MutableExecutionContext<*, *>>()
            .withDefaultCompute { NodeExecutionContext<Any, Any>() }
        // стек текущих обрабатываемых нод (ноды, у которых еще не обработаны дети)
        val traverseStack = ArrayDeque<GraphNode<*, *>>(sources)

        while (traverseStack.isNotEmpty()) {
            val currentNode = traverseStack.last()

            if (traversedNodes[currentNode.id]?.state == TraverseState.IN_PROGRESS) {
                traverseStack.removeLast()
                traversedNodes[currentNode.id]?.state = TraverseState.FINISHED
                continue
            }

            traversedNodes[currentNode.id] = NodeMetaInfo(
                transformNode(currentNode, executionContexts).also {
                    // добавляем у родителя ссылку на только что трансформированную ноду
                    addChildLinkToParent(
                        it,
                        executionContexts[it.id],
                        traversedNodes.mapValues { it.value.node }
                    )
                }
            )

            val childrenByState = currentNode
                .outputNodes
                .groupBy { traversedNodes[it.id]?.state ?: TraverseState.NEW }

            childrenByState[TraverseState.IN_PROGRESS]?.forEach {
                val cycleStartNodeId = findCycleStart(it, traverseStack, traversedNodes)
                executionContexts[it.id]?.registerCycle(cycleStartNodeId, currentNode.id)
            }

            childrenByState[TraverseState.NEW]?.forEach {
                traverseStack.addLast(it)
            }
        }

        return traversedNodes.mapValues { it.value.node }
    }

    /**
     * Finds next node of cycle after cycleStartNode
     */
    private fun findCycleStart(
        cycleStartNode: GraphNode<*, *>,
        traverseStack: ArrayDeque<GraphNode<*, *>>,
        traversedNodes: Map<String, NodeMetaInfo>
    ) = traverseStack.asReversed()
        .map { it.id }
        .takeWhile { it != cycleStartNode.id }
        .last { it in traversedNodes }
}


