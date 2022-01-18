package ru.nsu.convoyeur.examples

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.StatefulSinkNode
import ru.nsu.convoyeur.core.declaration.graph.SourceNode
import ru.nsu.convoyeur.core.declaration.graph.StatefulTransformNode
import ru.nsu.convoyeur.core.declaration.graph.emit

class SeveralSourcesExample : ConvoyeurExample<Int>() {
    override fun getDeclarationGraph(): List<SourceGraphNode<Int>> {
        val sourceNode = SourceNode<Int>(
            id = "source-id",
            action = {
                repeat(10) {
                    println("[SOURCE] Send to map $it")
                    emit("map-id", it)
                }
                println("[SOURCE] FINISH")
            })

        val secondSourceNode = SourceNode<Int>(
            id = "second-source-id",
            action = {
                (20..30).forEach {
                    println("[SOURCE-2] Send to map $it")
                    emit("map-id", it)
                }
                println("[SOURCE-2] FINISH")
            })

        val mapNode = StatefulTransformNode<Int, String>(
            id = "map-id",
            // можно над апи подумать, мб стоит в момент линковки с детьми буфф сайз прописывать
            bufferSizes = mapOf(
                "source-id" to 4,
                "second-source-id" to 2
            ),
            action = {
                val inputChan = inputChannel("source-id")
                inputChan?.consumeEach {
                    println("[MAP] Sending to sink $it")
                    emit("sink-id", "Mapped [$it]")
                }
                inputChannel("second-source-id")?.consumeEach {
                    println("[MAP-2] Sending to sink $it")
                    emit("sink-id", "Mapped-2 [$it]")
                }
                println("[MAP] FINISH")
            })

        val filterNode = StatefulTransformNode<Int, String>(
            id = "filter-id",
            action = {
                println("[FILTER] ${Thread.currentThread().name}")
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
            action = {
                // тут юзер волен задать джойн как ему угодно
                // для простоты тут игрушечный пример для конечных стримов

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
                    delay(400)
                    println("[SINK] Get from '${result.first}': ${result.second}")
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
        secondSourceNode.outputNodes = outputNodes

        return listOf(sourceNode, secondSourceNode)
    }
}