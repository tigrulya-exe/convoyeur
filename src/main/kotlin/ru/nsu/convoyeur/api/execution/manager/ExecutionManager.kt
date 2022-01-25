package ru.nsu.convoyeur.api.execution.manager

import kotlinx.coroutines.Job
import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode

interface ExecutionManager {
    fun <V> execute(sources: List<SourceGraphNode<V>>)

    fun <V> executeAsync(sources: List<SourceGraphNode<V>>): Job

    fun shutdown()
}