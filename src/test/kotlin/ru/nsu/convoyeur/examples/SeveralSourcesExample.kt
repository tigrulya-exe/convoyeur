package ru.nsu.convoyeur.examples

import kotlinx.coroutines.delay
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.*

/**
 * source1 -> filter -> sink
 *          \       /
 *           \    /
 * source2 -> map
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
            bufferSizes = mapOf(
                "source-id" to 4,
                "second-source-id" to 2
            )
        ) { channelName, value ->
            println("[MAP] Map $value from '$channelName'")
            emit("Mapped from '$channelName' [$value]")
        }

        val filterNode = TransformNode<Int, String>("filter-id") { _, value ->
            println("[FILTER] Filter $value")
            if (value % 2 == 0) {
                emit("Filtered [$value]")
            }
        }

        val sinkNode = SinkNode<String>(
            "sink-id",
            onChannelClose = { println("Channel $it close") }
        ) { channelName, value ->
            println("[SINK] Get value '$value' from channel '$channelName")
            // check backpressure
            delay(1000)
        }

        val commonGraph = arrayOf(
            mapNode.goesTo(sinkNode),
            filterNode.goesTo(sinkNode)
        )

        sourceNode.goesVia(*commonGraph)
        secondSourceNode.goesVia(*commonGraph)

        return listOf(sourceNode, secondSourceNode)
    }
}