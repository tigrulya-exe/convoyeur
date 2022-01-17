package ru.nsu.convoyeur.examples

import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.SinkNode
import ru.nsu.convoyeur.core.declaration.graph.TransformNode
import ru.nsu.convoyeur.core.declaration.graph.asSourceNode
import ru.nsu.convoyeur.core.declaration.graph.emit

class StatelessNodesExample : ConvoyeurExample<Int>() {
    override fun getDeclarationGraph(): List<SourceGraphNode<Int>> {
        val sourceNode = (1..10).asSourceNode()

        val filterNode = TransformNode<Int, Int>(
            id = "filter",
            callback = { _, value ->
                if (value?.rem(2) == 0) {
                    emit(value)
                }
            }
        )

        val mapNode = TransformNode<Int, String>(
            id = "map",
            callback = { _, value ->
                value?.let {
                    emit("Mapped[$it]")
                }
            }
        )

        val sinkNode = SinkNode<String>(
            id = "sink",
            callback = { channelName, value ->
                println("[sink] Get '$value' from channel '$channelName'")
            }
        )

        sourceNode.outputNodes = listOf(filterNode)
        filterNode.outputNodes = listOf(mapNode)
        mapNode.outputNodes = listOf(sinkNode)
        return listOf(sourceNode);
    }
}