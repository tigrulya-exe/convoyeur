package ru.nsu.convoyeur.examples

import kotlinx.coroutines.channels.consumeEach
import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.*

/**
 * source -> filter -> map -> sink
 */
class LinearGraphExample : ConvoyeurExample<Int>() {
    override fun getDeclarationGraph(): List<SourceGraphNode<Int>> {
        val sourceNode = SourceNode<Int>("source-id") {
            repeat(10) {
                // send value to channel 'filter-id'
                emit(it)
            }
            println("[SOURCE] FINISH")
        }

        // stateful transform node
        val filterNode = StatefulTransformNode<Int, String>(
            id = "filter-id",
            action = {
                // in such nodes we can use
                var someState = 0
                // get input channel by name
                val inputChan = inputChannel("source-id")
                inputChan?.consumeEach {
                    if (it % 2 == 0) {
                        println("[FILTER] Sending to map $it")
                        emit("map-id", "Filtered [$it] + state[$someState]")
                    }
                    someState = (0..1000).random()
                }
                println("[FILTER] FINISH")
            })

        // stateless (except closure variables) transform node (with both inputs and outputs)
        val mapNode = TransformNode<String, String>(
            "map-id",
            // define buffer sizes of each channel (default is 1)
            bufferSizes = mutableMapOf("source-id" to 2)
        ) { _, value ->
            // send value to first channel
            emit("Mapped [$value]")
        }

        val sinkNode = SinkNode<String>(
            onChannelClose = { println("Channel $it close") }
        ) { channelName, value ->
            println("[SINK] Get value '$value' from channel '$channelName")
        }

        sourceNode.via(filterNode)
            .via(mapNode)
            .to(sinkNode)

        return listOf(sourceNode)
    }
}