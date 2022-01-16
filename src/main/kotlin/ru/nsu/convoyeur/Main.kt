package ru.nsu.convoyeur

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.selects.select
import ru.nsu.convoyeur.core.declaration.graph.SinkNode
import ru.nsu.convoyeur.core.declaration.graph.SourceNode
import ru.nsu.convoyeur.core.declaration.graph.TransformNode
import ru.nsu.convoyeur.core.declaration.graph.emit
import ru.nsu.convoyeur.core.execution.DefaultExecutionManager

// declaration graph
// source -> map -> sink

// execution graph with parallelism
// source -> map -> sink
//        -> map ->
//        -> map ->
//        -> map ->

fun main() {
    val sourceNode = SourceNode<Int>(
        id = "source-id",
        producer = {
            repeat(10) {
                println("[SOURCE] Send to map $it")
                emit("map-id", it)
                println("[SOURCE] Send to filter $it")
                emit("filter-id", it)
            }
            println("[SOURCE] FINISH")
        })

    val secondSourceNode = SourceNode<Int>(
        id = "second-source-id",
        producer = {
            (20..30).forEach {
                println("PRODUCING VALUE $it")
                emit("map-id", it)
//                emit("filter-id", it)
            }
        })

    val mapNode = TransformNode<Int, String>(
        id = "map-id",
//        bufferSize = 4,
        transform = {
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

    val filterNode = TransformNode<Int, String>(
        id = "filter-id",
//        bufferSize = 6,
        transform = {
            val inputChan = inputChannel("source-id")
            inputChan?.consumeEach {
                println("[FILTER] Get $it")
                if (it % 2 == 0) {
                    println("[FILTER] Sending to sink $it")
                    emit("sink-id", "Filtered [$it]")
                }
            }
            println("[FILTER] FINISH")
        })

    val sinkNode = SinkNode<String>(
        id = "sink-id",
//        bufferSize = 16,
        consumer = {
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

//    val executionGraph = DefaultExecutionGraphBuilder(CoroutineChannelFactory())
//        .build(listOf(
//            sourceNode,
////            secondSourceNode
//        ))

    DefaultExecutionManager().execute(listOf(sourceNode, secondSourceNode))
}
