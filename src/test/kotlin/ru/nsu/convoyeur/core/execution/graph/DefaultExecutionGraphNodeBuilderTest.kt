package ru.nsu.convoyeur.core.execution.graph

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.nsu.convoyeur.core.declaration.graph.SinkNode
import ru.nsu.convoyeur.core.declaration.graph.StatefulSinkNode
import ru.nsu.convoyeur.core.execution.context.NodeExecutionContext
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultExecutionGraphNodeBuilderTest {

    companion object {
        const val SOURCE_ID = "source"
        const val SINK_ID = "sink"
        const val REPEAT_TIMES = 10
    }

    private val nodeBuilder = DefaultExecutionGraphNodeBuilder()

    @Test
    fun buildStatelessNode() {
        var counter = 0
        val inputNode = SinkNode<String>(
            id = SOURCE_ID,
            action = { _, _ -> counter++ },
        )

        val inputChannel = Channel<String>()
        val expectedContext = NodeExecutionContext<String, Nothing>(
            inputChannels = mutableMapOf("" to inputChannel)
        ).apply { isActive = true }
        val result = nodeBuilder.build(inputNode, expectedContext)

        with(result) {
            assertEquals(context, expectedContext)
            assertEquals(id, SOURCE_ID)
        }

        runBlocking {
            launch {
                repeat(REPEAT_TIMES) { inputChannel.send("") }
                inputChannel.close()
            }
            launch {
                result.action(this)
            }
        }

        assertEquals(REPEAT_TIMES, counter)
    }

    @Test
    fun buildStatefulNode() {
        var container: String? = null

        val inputNode = StatefulSinkNode<String>(
            id = SINK_ID,
            action = { container = SINK_ID },
        )

        val expectedContext = NodeExecutionContext<String, Nothing>()
        val result = nodeBuilder.build(inputNode, expectedContext)

        with(result) {
            assertEquals(context, expectedContext)
            assertEquals(id, SINK_ID)
        }

        runBlocking {
            result.action(this)
        }

        assertEquals(SINK_ID, container)
    }
}