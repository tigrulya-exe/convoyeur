package ru.nsu.convoyeur.examples

import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.extension.asSourceNode
import ru.nsu.convoyeur.core.declaration.graph.SinkNode
import ru.nsu.convoyeur.core.declaration.graph.TransformNode
import ru.nsu.convoyeur.core.declaration.graph.emit

/**
 * source -> filter -> map -> sink
 */
class StatelessNodesExample : ConvoyeurExample<Int>() {
    override fun getDeclarationGraph(): List<SourceGraphNode<Int>> {
        val sourceNode = (1..10).asSourceNode()

        val filterNode = TransformNode<Int, Int>(
            id = "filter",
            action = { _, value ->
                if (value.rem(2) == 0) {
                    emit(value)
                }
            },
            onChannelClose = { println("[filter] channel $it closed") }
        )

        val mapNode = TransformNode<Int, String>(
            id = "map",
            action = { _, value ->
                emit("Mapped[$value]")
            },
            onChannelClose = { println("[map] channel $it closed") }
        )

        val sinkNode = SinkNode<String>(
            id = "sink",
            action = { channelName, value ->
                println("[sink] Get '$value' from channel '$channelName'")
            },
            onChannelClose = { println("[sink] channel $it closed") }
        )

        sourceNode
            .via(filterNode)
            .via(mapNode)
            .to(sinkNode)

        return listOf(sourceNode);
    }
}