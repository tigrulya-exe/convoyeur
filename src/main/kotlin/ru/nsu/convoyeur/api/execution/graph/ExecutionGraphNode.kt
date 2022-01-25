package ru.nsu.convoyeur.api.execution.graph

import kotlinx.coroutines.CoroutineScope
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext

typealias ContextEnrichedAction = suspend (CoroutineScope) -> Unit

interface ExecutionGraphNode<S, D> {
    val id: String
    val action: ContextEnrichedAction
    val children: MutableMap<String, ExecutionGraphNode<D, *>>
    val context: MutableExecutionContext<S, D>
}