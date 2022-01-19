package ru.nsu.convoyeur.examples

import kotlinx.coroutines.runBlocking
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.core.execution.DefaultExecutionManager
import kotlin.test.Test

abstract class ConvoyeurExample<S> {
    private val executionManager = DefaultExecutionManager()

    @Test
    fun execute() {
        executionManager.execute(
            getDeclarationGraph()
        )
    }

    @Test
    fun executeSimpleExampleAsync() = runBlocking {
        val handle = executionManager.executeAsync(
            getDeclarationGraph()
        )

        handle.invokeOnCompletion {
            println("Task completed")
        }

        handle.join()
    }


    protected abstract fun getDeclarationGraph(): List<SourceGraphNode<S>>
}