package ru.nsu.convoyeur.core.execution

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphBuilder
import ru.nsu.convoyeur.api.execution.manager.ExecutionManager
import ru.nsu.convoyeur.api.execution.manager.JobHandle
import ru.nsu.convoyeur.core.execution.graph.DefaultExecutionGraphBuilder
import ru.nsu.convoyeur.core.execution.graph.ExecutionGraph

class DefaultExecutionManager(
    private val executionGraphBuilder: ExecutionGraphBuilder = DefaultExecutionGraphBuilder()
) : ExecutionManager {

    private val executionGraphs = mutableMapOf<String, ExecutionGraph>()
    private val jobs = mutableMapOf<String, MutableMap<String, Job>>()

    override fun <V> execute(sources: List<SourceGraphNode<V>>): JobHandle {
        val executionGraph = executionGraphBuilder.build(sources)
        return execute(executionGraph)
    }

    private fun execute(executionGraph: ExecutionGraph): JobHandle = runBlocking {
        val nodeJobs = executionGraph.nodes
            .mapValues { launchNode(this, it.value) }
            .toMutableMap()

        JobHandle().also {
            jobs[it.id] = nodeJobs
            executionGraphs[it.id] = executionGraph
        }
    }

    private suspend fun launchNode(scope: CoroutineScope, node: ExecutionGraphNode<*, *>) = scope.launch {
        try {
            node.context.isActive = true
            node.action()
        } finally {
            node.context.isActive = false
            node.context.outputChannels.values.forEach {
                it.close()
            }
        }
    }
}