package ru.nsu.convoyeur

import ru.nsu.convoyeur.api.channel.key.asChannelKey
import ru.nsu.convoyeur.api.delegation.SinkNode
import ru.nsu.convoyeur.api.delegation.SourceNode
import ru.nsu.convoyeur.api.delegation.TransformNode

// declaration graph
// source -> map -> sink

// execution graph with parallelism
// source -> map -> sink
//        -> map ->
//        -> map ->
//        -> map ->

fun main() {
    val sourceNode = SourceNode<Int>({
        (1..10).forEach {
            println("PRODUCING VALUE $it")
            // TODO эти ChannelKey по-хорошему надо заменить на просто id нод
            emit("map_chan".asChannelKey(), it)
            emit("filter_chan".asChannelKey(), it)
        }
    })

    val mapNode = TransformNode<Int, String>({
        val inputChan = inputChannel("map_chan".asChannelKey())
        inputChan?.forEach {
            println("MAPPING VALUE $it")
            emit("map_sink_chan".asChannelKey(), "Mapped [$it]")
        }
    })

    val filterNode = TransformNode<Int, String>({
        val inputChan = inputChannel("filter_chan".asChannelKey())
        inputChan?.forEach {
            println("FILTERING VALUE $it")
            if (it % 2 == 0) {
                emit("filter_sink_chan".asChannelKey(), "Filtered [$it]")
            }
        }
    })

    val sinkNode = SinkNode<String>({
        val mapChan = inputChannel("map_sink_chan".asChannelKey())
        val filterChan = inputChannel("filter_sink_chan".asChannelKey())

        // тут юзер волен задать джойн как ему угодно
        // для простоты тут игрушечный пример для конечных стримов
        mapChan?.forEach {
            println("CONSUMING VALUE $it")
        }

        filterChan?.forEach {
            println("CONSUMING VALUE $it")
        }
    })

    mapNode.outputNodes = mapOf("".asChannelKey() to sinkNode)
    filterNode.outputNodes = mapOf("".asChannelKey() to sinkNode)
    sourceNode.outputNodes = mapOf(
        "".asChannelKey() to mapNode,
        "".asChannelKey() to filterNode,
    )
}
