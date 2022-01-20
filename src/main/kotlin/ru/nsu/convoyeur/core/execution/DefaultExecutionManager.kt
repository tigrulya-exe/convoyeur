package ru.nsu.convoyeur.core.execution

import kotlinx.coroutines.*
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphBuilder
import ru.nsu.convoyeur.api.execution.manager.ExecutionManager
import ru.nsu.convoyeur.api.execution.manager.JobHandle
import ru.nsu.convoyeur.core.execution.graph.CycleDetectingExecutionGraphBuilder
import ru.nsu.convoyeur.core.execution.graph.ExecutionGraph

class DefaultExecutionManager(
    private val executionGraphBuilder: ExecutionGraphBuilder = CycleDetectingExecutionGraphBuilder()
) : ExecutionManager {

    private val executionGraphs = mutableMapOf<String, ExecutionGraph>()
    private val jobs = mutableMapOf<String, MutableMap<String, Job>>()

    override fun <V> execute(sources: List<SourceGraphNode<V>>) = runBlocking {
        execute(this, sources)
    }

    override fun <V> executeAsync(sources: List<SourceGraphNode<V>>): Job = GlobalScope.launch {
        execute(this, sources)
    }

    private suspend fun <V> execute(scope: CoroutineScope, sources: List<SourceGraphNode<V>>) {
        val executionGraph = executionGraphBuilder.build(sources)
        val nodeJobs = executionGraph.nodes
            .mapValues { launchNode(scope, it.value) }
            .toMutableMap()

        JobHandle().also {
            jobs[it.id] = nodeJobs
            executionGraphs[it.id] = executionGraph
        }
    }

    private suspend fun launchNode(
        scope: CoroutineScope,
        node: ExecutionGraphNode<*, *>
    ) = scope.launch(Dispatchers.Default) {
        with(node) {
            try {
                context.isActive = true
                action()
//            } catch (e: Exception) {
//                println("EXCEPTION: $e")
            } finally {
                context.isActive = false
                context.outputChannels.values.forEach {
                    it.close()
                }
            }
        }
    }
}