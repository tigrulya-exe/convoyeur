package ru.nsu.convoyeur.core.execution.graph

import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import ru.nsu.convoyeur.api.execution.graph.ContextEnrichedAction
import ru.nsu.convoyeur.api.execution.graph.ExecutionGraphNode

class ExecutionGraphNodeImpl<S, D>(
    override val id: String,
    override val action: ContextEnrichedAction,
    override val context: MutableExecutionContext<S, D>,
    override val neighbours: MutableMap<String, ExecutionGraphNode<D, *>> = mutableMapOf()
) : ExecutionGraphNode<S, D>