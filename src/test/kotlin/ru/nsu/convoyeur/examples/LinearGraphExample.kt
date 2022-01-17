package ru.nsu.convoyeur.examples

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.StatefulSinkNode
import ru.nsu.convoyeur.core.declaration.graph.SourceNode
import ru.nsu.convoyeur.core.declaration.graph.StatefulTransformNode
import ru.nsu.convoyeur.core.declaration.graph.emit

class LinearGraphExample : ConvoyeurExample<Int>() {
    override fun getDeclarationGraph(): List<SourceGraphNode<Int>> {
        val sourceNode = SourceNode<Int>(
            id = "source-id",
            producer = {
                repeat(10) {
                    emit("map-id", it)
                    emit("filter-id", it)
                }
                println("[SOURCE] FINISH")
            })

        val mapNode = StatefulTransformNode<Int, String>(
            id = "map-id",
            transform = {
                val inputChan = inputChannel("source-id")
                inputChan?.consumeEach {
                    emit("sink-id", "Mapped [$it]")
                }

                println("[MAP] FINISH")
            })

        val filterNode = StatefulTransformNode<Int, String>(
            id = "filter-id",
            transform = {
                val inputChan = inputChannel("source-id")
                inputChan?.consumeEach {
                    if (it % 2 == 0) {
                        println("[FILTER] Sending to sink $it")
                        emit("sink-id", "Filtered [$it]")
                    }
                }
                println("[FILTER] FINISH")
            })

        val sinkNode = StatefulSinkNode<String>(
            id = "sink-id",
            consumer = {
                while (isActive && hasOpenInputChannels) {
                    val result = select<Pair<String, String?>> {
                        inputChannels()
                            .filter { !it.value.isClosedForReceive }
                            .forEach {
                                it.value.onReceiveCatching { result ->
                                    it.key to result.getOrNull()
                                }
                            }
                    }
                    delay(100)
                }
                println("[SINK] FINISH")
            })


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