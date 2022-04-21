package ru.nsu.convoyeur.examples

import kotlinx.coroutines.delay
import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.extension.asSourceNode
import ru.nsu.convoyeur.core.declaration.graph.*

/**
 * source1 -> filter -> sink1
 *          \       /
 *           \    /
 * source2 -> map -> sink2
 */
class SeveralSourcesExample : ConvoyeurExample<Int>() {
    override fun getDeclarationGraph(): List<SourceGraphNode<Int>> {
        val sourceNode = SourceNode<Int>("source-id") {
            repeat(10) {
                println("[SOURCE] Send $it")
                emit("map-id", it)
                emit("filter-id", it)
                delay(100)
            }
        }

        val secondSourceNode = (20..30).asSourceNode(id = "second-source-id")

        val mapNode = TransformNode<Int, String>(
            id = "map-id",
            bufferSizes = mutableMapOf(
                "source-id" to 4,
                "second-source-id" to 2
            )
        ) { channelName, value ->
            println("[MAP] Map $value from '$channelName'")
            "Mapped from '$channelName' [$value]".let {
                emit("sink1", it)
                emit("sink2", it)
            }
        }

        val filterNode = TransformNode<Int, String>("filter-id") { _, value ->
            println("[FILTER] Filter $value")
            if (value % 2 == 0) {
                emit("Filtered [$value]")
            }
        }

        val sinkNode = SinkNode<String>(
            "sink1",
            onChannelClose = { println("Channel $it close") }
        ) { channelName, value ->
            println("[SINK] Get value '$value' from channel '$channelName")
            // check backpressure
            delay(200)
        }

        val sinkNode2 = SinkNode<String>(
            "sink2",
            onChannelClose = { println("Channel $it close") }
        ) { channelName, value ->
            println("[SINK2] Get value '$value' from channel '$channelName")
            // check backpressure
            delay(100)
        }

        sourceNode.goesVia(
            mapNode.goesTo(sinkNode, sinkNode2),
            filterNode.goesTo(sinkNode)
        )
        secondSourceNode.via(mapNode)

        return listOf(sourceNode, secondSourceNode)
    }
}