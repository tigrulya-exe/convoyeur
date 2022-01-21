package ru.nsu.convoyeur.examples

import kotlinx.coroutines.channels.consumeEach
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.StatefulSinkNode
import ru.nsu.convoyeur.core.declaration.graph.SourceNode
import ru.nsu.convoyeur.core.declaration.graph.emit
import java.lang.RuntimeException

class ErrorHandlingExample : ConvoyeurExample<String>() {

    //TODO: WIP
    override fun getDeclarationGraph(): List<SourceGraphNode<String>> {
        val source = SourceNode<String>(
            id = "",
            action =  {
                repeat(10) {
                    emit("$it")
                }
                throw RuntimeException()
            }
        )

        val sinkNode = StatefulSinkNode<String>(
            id = "sink",
            action = {
                val inputChannel = inputChannel()
                inputChannel?.consumeEach {
                    println("sink - $it")
                }
            })

        source.goesTo(sinkNode)
        return listOf(source)
    }
}