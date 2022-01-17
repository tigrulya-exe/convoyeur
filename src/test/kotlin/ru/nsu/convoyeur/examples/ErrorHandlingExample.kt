package ru.nsu.convoyeur.examples

import kotlinx.coroutines.channels.consumeEach
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.StatefulSinkNode
import ru.nsu.convoyeur.core.declaration.graph.SourceNode
import ru.nsu.convoyeur.core.declaration.graph.emit
import java.lang.RuntimeException

class ErrorHandlingExample : ConvoyeurExample<String>() {

    override fun getDeclarationGraph(): List<SourceGraphNode<String>> {
        val source = SourceNode<String>(
            id = "",
            producer =  {
                repeat(10) {
                    emit("$it")
                }
                throw RuntimeException()
            }
        )

        val sinkNode = StatefulSinkNode<String>(
            id = "sink",
            consumer = {
                val inputChannel = inputChannel()
                inputChannel?.consumeEach {
                    println("sink - $it")
                }
            })

        source.outputNodes = listOf(sinkNode)
        return listOf(source)
    }
}