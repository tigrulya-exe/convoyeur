package ru.nsu.convoyeur.api.execution.graph

//TODO сюда полюбому надо добавить каналы
interface ExecutionNode<S, D> {
    val id: String
    val action: suspend () -> Unit
    val neighbours: MutableMap<String, ExecutionNode<D, *>>
}

class ExecutionGraphNode<S, D>(
    override val id: String,
    override val action: suspend () -> Unit,
    override val neighbours: MutableMap<String, ExecutionNode<D, *>> = mutableMapOf()
) : ExecutionNode<S, D> {
}
