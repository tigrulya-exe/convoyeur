package ru.nsu.convoyeur.examples

import kotlinx.coroutines.runBlocking
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.api.execution.graph.transform.ExecutionGraphBuilder
import ru.nsu.convoyeur.core.execution.DefaultExecutionManager
import ru.nsu.convoyeur.core.execution.graph.CycleDetectingExecutionGraphBuilder
import ru.nsu.convoyeur.core.execution.graph.SimpleBFSExecutionGraphBuilder
import ru.nsu.convoyeur.util.LoggerProperty
import kotlin.test.Test

abstract class ConvoyeurExample<S>(
    private val graphBuilders: List<ExecutionGraphBuilder> = listOf(
        CycleDetectingExecutionGraphBuilder(),
        SimpleBFSExecutionGraphBuilder()
    )
) {

    private val logger by LoggerProperty()

    @Test
    fun execute() {
        graphBuilders.forEach {
            logger.info("Test with ${it.javaClass}")
            DefaultExecutionManager(it).execute(
                getDeclarationGraph()
            )
        }
    }

    @Test
    fun executeSimpleExampleAsync() = runBlocking {
        graphBuilders.forEach {
            logger.info("Test with ${it.javaClass}")

            val handle = DefaultExecutionManager(it).executeAsync(
                getDeclarationGraph()
            )

            handle.invokeOnCompletion {
                logger.info("Task completed")
            }

            handle.join()
        }
    }


    protected abstract fun getDeclarationGraph(): List<SourceGraphNode<S>>
}