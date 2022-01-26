package ru.nsu.convoyeur.examples

import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.extension.asSourceNode
import ru.nsu.convoyeur.core.declaration.graph.SinkNode
import ru.nsu.convoyeur.core.declaration.graph.TransformNode
import ru.nsu.convoyeur.core.declaration.graph.emit
import ru.nsu.convoyeur.core.execution.graph.CycleDetectingExecutionGraphBuilder

/**
 *         map3 <-  map2
 *            \   /
 * source --> map1 --> sink
 */
class CycleExample : ConvoyeurExample<String>(
    listOf(CycleDetectingExecutionGraphBuilder())
) {
    override fun getDeclarationGraph(): List<SourceGraphNode<String>> {
        val sourceNode = listOf("a", "b", "c").asSourceNode()

        val mapNode = TransformNode<String, String>(
            id = "map1",
            onChannelClose = { channelName ->
                println("Closed $channelName")
            }
        ) { inputChannelName, value ->
            when (inputChannelName) {
                sourceNode.id -> {
                    println("get $value from source")
                    emit("map2", "MAP1[$value]")
                }
                "map3" -> {
                    println("get $value from cycle")
                    emit("sink", value)
                }
            }
        }

        val mapNode2 = TransformNode<String, String>("map2") { _, value ->
            emit("MAP2[$value]")
        }

        val mapNode3 = TransformNode<String, String>("map3") { _, value ->
            emit("MAP3[$value]")
        }

        val sinkNode = SinkNode<String>("sink") { _, value ->
            println("sink - $value")
        }

        sourceNode
            .via(mapNode).via(mapNode2).via(mapNode3).via(mapNode) // cycle
            .to(sinkNode)

        return listOf(sourceNode)
    }

}