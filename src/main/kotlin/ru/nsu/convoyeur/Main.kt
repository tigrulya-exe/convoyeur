package ru.nsu.convoyeur

import ru.nsu.convoyeur.api.declaration.SinkNode
import ru.nsu.convoyeur.api.declaration.SourceNode
import ru.nsu.convoyeur.api.declaration.TransformNode
import ru.nsu.convoyeur.api.declaration.emit
import ru.nsu.convoyeur.api.execution.channel.KotlinChannelFactory
import ru.nsu.convoyeur.api.execution.graph.transform.GraphTransformer

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

    sourceNode.outputNodes = listOf(
        mapNode.apply {
            outputNodes = listOf(sinkNode)
        },

        filterNode.apply {
            outputNodes = listOf(sinkNode)
        }
    )

    GraphTransformer(KotlinChannelFactory()).transform(sourceNode)
}
