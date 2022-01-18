package ru.nsu.convoyeur.examples

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.selects.select
import ru.nsu.convoyeur.api.declaration.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.StatefulSinkNode
import ru.nsu.convoyeur.core.declaration.graph.StatefulTransformNode
import ru.nsu.convoyeur.core.declaration.graph.asSourceNode
import ru.nsu.convoyeur.core.declaration.graph.emit


/**
 *         map3 <-  map2
 *            \   /
 * source --> map1 --> sink
 */
class CycleExample : ConvoyeurExample<String>() {
    override fun getDeclarationGraph(): List<SourceGraphNode<String>> {
        val sourceNode = listOf("a", "b", "c").asSourceNode()

        val mapNode = StatefulTransformNode<String, String>(
            id = "map1",
            action = {
                val sourceInputChan = inputChannel(sourceNode.id)
                val map3InputChan = inputChannel("map3")

                while (isActive && hasOpenInputChannels) {
                    select<Unit> {
                        if (!sourceInputChan?.isClosedForReceive!!) {
                            sourceInputChan?.onReceiveCatching {
                                println("get $it from source")
                                it.getOrNull()?.let {
                                    emit("map2", "MAP1[${it}]")
                                }
                            }
                        } else {
                            outputChannel("map2")?.close()
                        }

                        if (!map3InputChan?.isClosedForReceive!!) {
                            map3InputChan?.onReceiveCatching {
                                println("get $it from cycle")
                                emit("sink", it.getOrNull() ?: "error")
                            }
                        }
                    }
                }
            })

        val mapNode2 = StatefulTransformNode<String, String>(
            id = "map2",
            action = {
                val inputChannel = inputChannel()
                inputChannel?.consumeEach {
                    println("[map2] get $it from map")
                    emit("MAP2[$it]")
                }
            })

        val mapNode3 = StatefulTransformNode<String, String>(
            id = "map3",
            action = {
                val inputChannel = inputChannel()
                inputChannel?.consumeEach {
                    emit("MAP3[$it]")
                }
            })

        val sinkNode = StatefulSinkNode<String>(
            id = "sink",
            action = {
                val inputChannel = inputChannel()
                inputChannel?.consumeEach {
                    println("sink - $it")
                }
            })


        sourceNode.outputNodes = listOf(mapNode)
        mapNode.outputNodes = listOf(mapNode2, sinkNode)
        mapNode2.outputNodes = listOf(mapNode3)
        mapNode3.outputNodes = listOf(mapNode)

        return listOf(sourceNode);
    }

}