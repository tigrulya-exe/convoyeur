package ru.nsu.convoyeur.core.execution

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphBuilder
import ru.nsu.convoyeur.api.execution.manager.ExecutionManager
import ru.nsu.convoyeur.api.execution.manager.JobHandle
import ru.nsu.convoyeur.core.execution.graph.DefaultExecutionGraphBuilder
import ru.nsu.convoyeur.core.execution.graph.ExecutionGraph

class DefaultExecutionManager(
    private val executionGraphBuilder: ExecutionGraphBuilder = DefaultExecutionGraphBuilder()
) : ExecutionManager {

    private val executionGraphs = mutableMapOf<String, ExecutionGraph>()

    override fun <V> execute(sources: List<SourceGraphNode<V>>): JobHandle {
        val executionGraph = executionGraphBuilder.build(sources)
        return JobHandle().also {
            execute(executionGraph)
            executionGraphs[it.id] = executionGraph
        }
    }

    private fun execute(executionGraph: ExecutionGraph) = runBlocking {
        executionGraph.nodes
            .values
            .map { it.action }
            .forEach {
                async {
                    it()
                }
            }
    }
}