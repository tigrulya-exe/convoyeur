package ru.nsu.convoyeur.core.declaration.extension

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nsu.convoyeur.api.declaration.graph.SourceGraphNode
import ru.nsu.convoyeur.core.declaration.graph.SourceNode
import ru.nsu.convoyeur.core.declaration.graph.emit
import java.io.File
import java.util.*

fun <V> Iterable<V>.asSourceNode(
    id: String = UUID.randomUUID().toString(),
    outputChannelName: String? = null
): SourceGraphNode<V> {
    return SourceNode(
        id = id,
        action = {
            this@asSourceNode.forEach {
                outputChannelName?.let { name ->
                    emit(name, it)
                } ?: emit(it)
            }
        }
    )
}

fun File.asSourceNode(
    id: String = UUID.randomUUID().toString(),
    outputChannelName: String? = null
): SourceGraphNode<String> {
    return SourceNode(id) {
        withContext(Dispatchers.IO) {
            bufferedReader().use {
                it.lineSequence().forEach { line ->
                    outputChannelName?.let { name ->
                        emit(name, line)
                    } ?: emit(line)
                }
            }
        }
    }
}


