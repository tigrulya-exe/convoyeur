package ru.nsu.convoyeur.examples

import ru.nsu.convoyeur.core.declaration.extension.convoyeur
import java.util.stream.Collectors
import kotlin.test.Test

class StreamExample {
    @Test
    fun example() {
        val result = (1..10).convoyeur()
            .filter { it % 2 == 0 }
            .map { "mapped $it" }
            .peek { println("[${Thread.currentThread().name}] Hello from coroutine! - $it") }
            .peek { println("[${Thread.currentThread().name}] Hello from another coroutine! - $it") }
            .collect(Collectors.toList())

        println(result)
    }
}