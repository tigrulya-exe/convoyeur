package ru.nsu.convoyeur.examples

import kotlinx.coroutines.channels.consumeEach
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.*
import kotlin.random.Random

/**
 * source -> map -> sink
 *        \      /
 *         filter
 */
class LinearGraphExample : ConvoyeurExample<Int>() {
    override fun getDeclarationGraph(): List<SourceGraphNode<Int>> {
        val sourceNode = SourceNode<Int>("source-id") {
            repeat(10) {
                // send value to channel 'map-id'
                emit("map-id", it)
                emit("filter-id", it)
            }
            println("[SOURCE] FINISH")
        }

        // stateless (except closure variables) transform node (with both inputs and outputs)
        val mapNode = TransformNode<Int, String>(
            // define buffer sizes of each channel (default is 1)
            bufferSizes = mapOf("source-id" to 2)
        ) { _, value ->
            // send value to first channel
            emit("Mapped [$value]")
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
                        println("[FILTER] Sending to sink $it")
                        emit("sink-id", "Filtered [$it] + state[$someState]")
                    }
                    someState = (0..1000).random()
                }
                println("[FILTER] FINISH")
            })

        val sinkNode = SinkNode<String>(
            "sink-id",
            onChannelClose = { println("Channel $it close") }
        ) { channelName, value ->
            println("[SINK] Get value '$value' from channel '$channelName")
        }

        val outputNodes = listOf(
            mapNode.apply {
                outputNodes = listOf(sinkNode)
            },

            filterNode.apply {
                outputNodes = listOf(sinkNode)
            }
        )

        sourceNode.outputNodes = outputNodes

        return listOf(sourceNode)
    }
}