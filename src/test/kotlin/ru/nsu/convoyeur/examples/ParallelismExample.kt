package ru.nsu.convoyeur.examples

import io.kotest.inspectors.forAllValues
import kotlinx.coroutines.selects.select
import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.*
import kotlin.random.Random

class ParallelismExample : ConvoyeurExample<Int>() {
    override fun getDeclarationGraph(): List<SourceGraphNode<Int>> {
        val sourceNode1 = SourceNode<Int>(
            "source1_9",
            parallelism = 3
        ) {
            generateSequence (parallelIndex + 1) { it + 3}
                .take(3)
                .forEach { number ->
                    emit(number)
                }
            println("[SOURCE] FINISH")
        }

        val sourceNode2 = SourceNode<Int> (
            "source10_19",
            parallelism = 2
        ) {
            generateSequence (parallelIndex + 10) { it + 2}
                .take(5)
                .forEach { number ->
                    emit(number)
                }
            println("[SOURCE] FINISH")
        }

        val mapNode = StatefulTransformNode<Int, String> (
            "randomStringMapper",
            parallelism = 4
        ) {
            val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            select<Unit> {
                inputChannels()
                    .forAllValues { channel ->
                        channel.onReceive { length ->
                            val randomString = (1..length)
                                .map { Random.nextInt(0, charPool.size) }
                                .map(charPool::get)
                                .joinToString("")
                            emit(randomString)
                        }
                    }
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