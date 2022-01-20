package ru.nsu.convoyeur.core.execution.graph

import kotlinx.coroutines.channels.SendChannel
import ru.nsu.convoyeur.api.channel.ChannelFactory
import ru.nsu.convoyeur.api.declaration.GraphNode
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
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
        val outputChannels = mutableMapOf<String, SendChannel<D>>()

        for (child in node.outputNodes) {
            val childContext = executionContexts[child.id] as NodeExecutionContext<D, Any>
            val channel = channelFactory.createChannel<D>(
                child.bufferSizes.getOrDefault(node.id, ChannelFactory.DEFAULT_BUFF_SIZE)
            )
            outputChannels[child.id] = channel
            childContext.addInputChannel(
                nodeId = node.id,
                channel = channel
            )
        }

        val context = executionContexts[node.id] as NodeExecutionContext<S, D>
        return nodesTransformer.build(node, context.also { it.outputChannels = outputChannels })
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