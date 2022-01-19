package ru.nsu.convoyeur.examples

import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.SinkNode
import ru.nsu.convoyeur.core.declaration.graph.TransformNode
import ru.nsu.convoyeur.core.declaration.graph.asSourceNode
import ru.nsu.convoyeur.core.declaration.graph.emit

/**
 *         map3 <-  map2
 *            \   /
 * source --> map1 --> sink
 */
class CycleExample : ConvoyeurExample<String>() {
    override fun getDeclarationGraph(): List<SourceGraphNode<String>> {
        val sourceNode = listOf("a", "b", "c").asSourceNode()

        val mapNode = TransformNode<String, String>(
            id = "map1",
            action = { channelName, value ->
                when (channelName) {
                    sourceNode.id -> {
                        println("get $value from source")
                        emit("map2", "MAP1[$value]")
                    }
                    "map3" -> {
                        println("get $value from cycle")
                        emit("sink", value)
                    }
                }
            },
            onChannelClose = { channelName ->
                if (channelName == sourceNode.id) {
                    outputChannel("map2")?.close()
                }
            }

        )

        val mapNode2 = TransformNode<String, String>(
            id = "map2",
            action = { _, value -> emit("MAP2[$value]") }
        )

        val mapNode3 = TransformNode<String, String>(
            id = "map3",
            action = { _, value -> emit("MAP3[$value]") }
        )

        val sinkNode = SinkNode<String>(
            id = "sink",
            action = { _, value -> println("sink - $value") }
        )

        sourceNode.outputNodes = listOf(mapNode)
        mapNode.outputNodes = listOf(mapNode2, sinkNode)
        mapNode2.outputNodes = listOf(mapNode3)
        mapNode3.outputNodes = listOf(mapNode)

        return listOf(sourceNode);
    }

}