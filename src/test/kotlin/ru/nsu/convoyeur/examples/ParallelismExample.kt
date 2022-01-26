package ru.nsu.convoyeur.examples

import kotlinx.coroutines.channels.consumeEach
import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.*

class ParallelismExample : ConvoyeurExample<Int>() {
    override fun getDeclarationGraph(): List<SourceGraphNode<Int>> {
        val sourceNode1 = SourceNode<Int>(
            "source1",
            parallelism = 3
        ) {
            (parallelIndex + 1 .. 10 step 3)
                .forEach { number ->
                    emit(number)
                }
            println("[SOURCE 1] FINISH")
        }

        val sourceNode2 = SourceNode<Int> (
            "source2",
            parallelism = 2
        ) {
            (parallelIndex + 10 .. 20 step 2)
                .forEach { number ->
                    emit(number)
                }
            println("[SOURCE 2] FINISH")
        }

        val mapNode = StatefulTransformNode<Int, String> (
            "randomStringMapper",
            parallelism = 2
        ) {
            val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            val channelName = "source" + (parallelIndex + 1)
            inputChannel(channelName)
                ?.consumeEach { length ->
                    val randomString = (1..length)
                        .map(charPool::get)
                        .joinToString("")
                    emit( randomString)
                }
        }

        val sinkNode = SinkNode<String> (
            "printSink"
        ) { _, value ->
            println("[SINK2] Get value '$value' from input channel.")
        }

        sourceNode1.via(mapNode).to(sinkNode)
        sourceNode2.via(mapNode)

        return listOf(sourceNode1, sourceNode2)
    }
}