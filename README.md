# Convoyeur

Convoyeur is a simple framework for data pipelines processing. 

[Presentation with main framework features (in russian)](https://docs.google.com/presentation/d/1HkRfrlQrwQb7xZ4_6zdGLWVPhAbmxuTFSo7Xd8VXbww/edit?usp=sharing).

## Installation
[![](https://jitpack.io/v/tigrulya-exe/convoyeur.svg)](https://jitpack.io/#tigrulya-exe/convoyeur)<br>
[Maven/Gradle import instructions](https://jitpack.io/#tigrulya-exe/convoyeur) 

## DeclarationGraph API

Convoyeur provides flexible API for constructing data processing graph:

```kotlin
val sourceNode = SourceNode<Int>("source-id") {
    repeat(10) {
        // send value to channel 'filter-id'
        emit(it)
    }
}

// stateful transform node
val filterNode = StatefulTransformNode<Int, String>("filter-id") {
    // in such nodes we can use
    var someState = 0
    // get input channel by name
    val inputChan = inputChannel("source-id")
    inputChan?.consumeEach {
      if (it % 2 == 0) {
        emit("map-id", "Filtered [$it] + state[$someState]")
      }
      someState = (0..1000).random()
    }
}

// stateless (except closure variables) transform node (with both inputs and outputs)
val mapNode = TransformNode<String, String>("map-id") { _, value ->
    // send value to first channel
    emit("Mapped [$value]")
}

val sinkNode = SinkNode<String>(
    onChannelClose = { println("Channel $it close") }
) { channelName, value ->
    println("[SINK] Get value '$value' from channel '$channelName")
}

// build graph by connecting nodes
sourceNode.via(filterNode)
    // define node input channel buffersize
    .via(mapNode, bufferSize = 16)
    .to(sinkNode)

// execute blocking 
DefaultExecutionManager().execute(
    listOf(sourceNode)
)
```

## Convoyeur API

Convoyeur also provides more concise stream-like API for Java `Iterable<V>` and `File`:

```kotlin
val result = (1..10).convoyeur()
            .filter { it % 2 == 0 }
            .map { "mapped $it" }
            .peek { println("[${Thread.currentThread().name}] Hello from coroutine! - $it") }
            .peek { println("[${Thread.currentThread().name}] Hello from another coroutine! - $it") }
            .collect(Collectors.toList()) // supports Stream API collectors  
```
Each operator transforms to appropriate DeclarationGraph node at first, then to internal execution graph node 
and finally it's executed on a separate coroutine.

You can find more examples [here](https://github.com/tigrulya-exe/convoyeur/tree/master/src/test/kotlin/ru/nsu/convoyeur/examples).
