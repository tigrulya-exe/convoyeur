package ru.nsu.convoyeur.core.execution.graph

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import ru.nsu.convoyeur.core.declaration.graph.SinkNode
import ru.nsu.convoyeur.core.declaration.graph.asSourceNode
import kotlin.test.Test

class SimpleBFSExecutionGraphBuilderTest {

    private val graphBuilder = SimpleBFSExecutionGraphBuilder()

    @Test
    fun buildSimpleGraph() {
        val sink = SinkNode<Int> { _, value -> println(value) }
        val source = (1..3)
            .asSourceNode()
            .apply {
                outputNodes = mutableListOf(sink)
            }

        val executionGraph = graphBuilder.build(source)
        executionGraph.sourceIds shouldContainExactly listOf(source.id)
        executionGraph.nodes
            .values
            .map { it.id } shouldContainExactly listOf(source.id, sink.id)

        val sourceContext = executionGraph.nodes[source.id]?.context
        sourceContext?.shouldNotBeNull()
        sourceContext?.let {
            it.outputChannels.keys shouldContainExactly setOf(sink.id)
            it.inputChannels.shouldBeEmpty()
        }

        val sinkContext = executionGraph.nodes[sink.id]?.context
        sinkContext?.shouldNotBeNull()
        sinkContext?.let {
            it.inputChannels.keys shouldContainExactly setOf(source.id)
            it.outputChannels.shouldBeEmpty()
        }
    }
}