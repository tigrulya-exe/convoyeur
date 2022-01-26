package ru.nsu.convoyeur.core.declaration.extension

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nsu.convoyeur.api.declaration.graph.SinkGraphNode
import ru.nsu.convoyeur.core.declaration.graph.SinkNode
import java.io.File
import java.util.*

fun <V> File.asSinkNode(
    id: String = UUID.randomUUID().toString(),
    outputChannelName: String? = null
): SinkGraphNode<V> {
    val writer = bufferedWriter()
    return SinkNode(
        id = id,
        onChannelClose = {
            withContext(Dispatchers.IO) {
                writer.close()
            }
        }
    ) { _, value ->
        withContext(Dispatchers.IO) {
            writer.appendLine("$value")
        }
    }
}