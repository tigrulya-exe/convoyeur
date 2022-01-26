package ru.nsu.convoyeur.core.execution.graph

import ru.nsu.convoyeur.api.channel.ChannelFactory
import ru.nsu.convoyeur.api.channel.ChannelFactory.Companion.DEFAULT_BUFF_SIZE
import ru.nsu.convoyeur.api.declaration.graph.GraphNode
import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphBuilder
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphNodeBuilder
import ru.nsu.convoyeur.core.channel.CoroutineChannelFactory
import ru.nsu.convoyeur.core.execution.context.NodeExecutionContext

@Suppress("UNCHECKED_CAST")
abstract class AbstractExecutionGraphBuilder(
    private val channelFactory: ChannelFactory = CoroutineChannelFactory(),
    private var nodesTransformer: ExecutionGraphNodeBuilder = DefaultExecutionGraphNodeBuilder()
) : ExecutionGraphBuilder {
    override fun <V> build(source: SourceGraphNode<V>) = build(listOf(source))

    override fun <V> build(sources: List<SourceGraphNode<V>>): ExecutionGraph {
        return ExecutionGraph(
            nodes = traverseGraph(sources),
            sourceIds = sources.map { it.id }
        )
    }

    protected abstract fun traverseGraph(sources: List<SourceGraphNode<*>>): Map<String, ExecutionGraphNode<*, *>>

    protected fun <S, D> transformNode(
        node: GraphNode<S, D>,
        executionContexts: MutableMap<String, MutableExecutionContext<*, *>>,
    ): ExecutionGraphNode<out S, out D> {
        val context = executionContexts[node.id] as NodeExecutionContext<S, D>
        for (child in node.outputNodes) {
            val childContext = executionContexts[child.id] as NodeExecutionContext<D, Any>
            val multiplyFactor = child.parallelism * node.parallelism
            val bufferSize = child.bufferSizes.getOrDefault(node.id, DEFAULT_BUFF_SIZE) * multiplyFactor
            val channel = channelFactory.createChannel<D>(bufferSize)
            context.addOutputChannel(
                nodeId = child.id,
                channel = channel
            )
            childContext.addInputChannel(
                nodeId = node.id,
                channel = channel
            )
        }

        return nodesTransformer.build(node, context)
    }

    protected fun <S, D> addChildLinkToParent(
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