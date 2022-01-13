package ru.nsu.convoyeur

import ru.nsu.convoyeur.core.declaration.graph.SinkNode
import ru.nsu.convoyeur.core.declaration.graph.SourceNode
import ru.nsu.convoyeur.core.declaration.graph.TransformNode
import ru.nsu.convoyeur.core.declaration.graph.emit
import ru.nsu.convoyeur.core.channel.CoroutineDataChannelFactory
import ru.nsu.convoyeur.api.execution.graph.transform.GraphTransformer
import ru.nsu.convoyeur.core.execution.graph.DefaultGraphTransformer

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
            (1..10).forEach {
                println("PRODUCING VALUE $it")
                emit("map-id", it)
                emit("filter-id", it)
            }
        })

    val secondSourceNode = SourceNode<Int>(
        id = "second-source-id",
        producer = {
            (20..30).forEach {
                println("PRODUCING VALUE $it")
                emit("map-id", it)
                emit("filter-id", it)
            }
        })


    val mapNode = TransformNode<Int, String>(
        id = "map-id",
        transform = {
            val inputChan = inputChannel("source-id")
            inputChan?.forEach {
                println("MAPPING VALUE $it")
                emit("sink-id", "Mapped [$it]")
            }
        })

    val filterNode = TransformNode<Int, String>(
        id = "filter-id",
        transform = {
            val inputChan = inputChannel("source-id")
            inputChan?.forEach {
                println("FILTERING VALUE $it")
                if (it % 2 == 0) {
                    emit("sink-id", "Filtered [$it]")
                }
            }
        })

    val sinkNode = SinkNode<String>(
        id = "sink-id",
        consumer = {
            val mapChan = inputChannel("map-id")
            val filterChan = inputChannel("sink-id")

            // тут юзер волен задать джойн как ему угодно
            // для простоты тут игрушечный пример для конечных стримов
            mapChan?.forEach {
                println("CONSUMING VALUE $it")
            }

            filterChan?.forEach {
                println("CONSUMING VALUE $it")
            }
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

    val executionSources = DefaultGraphTransformer(CoroutineDataChannelFactory())
        .transform(listOf(sourceNode, secondSourceNode))
    println(executionSources)
}
