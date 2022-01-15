package ru.nsu.convoyeur.api.execution.manager

import ru.nsu.convoyeur.api.declaration.SourceGraphNode

interface ExecutionManager {
    fun <V> execute(sources: List<SourceGraphNode<V>>): JobHandle
}