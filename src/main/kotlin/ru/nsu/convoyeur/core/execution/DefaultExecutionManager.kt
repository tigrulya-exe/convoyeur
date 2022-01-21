package ru.nsu.convoyeur.core.execution

import kotlinx.coroutines.*
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphBuilder
import ru.nsu.convoyeur.api.execution.manager.ExecutionManager
import ru.nsu.convoyeur.core.execution.graph.CycleDetectingExecutionGraphBuilder
import ru.nsu.convoyeur.core.execution.graph.ExecutionGraph

data class ExecutionGraphMetadata(val executionGraph: ExecutionGraph) {
    lateinit var job: Job
}

class DefaultExecutionManager(
    private val executionGraphBuilder: ExecutionGraphBuilder = CycleDetectingExecutionGraphBuilder()
) : ExecutionManager {

    private val executionGraphs = mutableMapOf<String, ExecutionGraphMetadata>()

    override fun <V> execute(sources: List<SourceGraphNode<V>>) = runBlocking {
        val executeJob = executeAsync(sources)
        executeJob.join()
    }

    override fun <V> executeAsync(sources: List<SourceGraphNode<V>>) =
        execute(executionGraphBuilder.build(sources))

    override fun shutdown() {
        runBlocking {
            executionGraphs.values
                .onEach { it.job.cancel() }
                .forEach { it.job.join() }
        }
    }

    private fun execute(executionGraph: ExecutionGraph) = GlobalScope.launch {
        executionGraph.nodes
            .forEach { launchNode(this, it.value) }
        executionGraphs[executionGraph.id] = ExecutionGraphMetadata(executionGraph)
    }.also { executionGraphs[executionGraph.id]?.job = it }

    private suspend fun launchNode(
        scope: CoroutineScope,
        node: ExecutionGraphNode<*, *>
    ) = scope.launch(Dispatchers.Default) {
        with(node) {
            try {
                context.isActive = true
                action()
            } finally {
                context.isActive = false
                context.outputChannels.values.forEach {
                    it.close()
                }
            }
        }
    }
}