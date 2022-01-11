package ru.nsu.convoyeur.api.channel

import kotlinx.coroutines.channels.Channel

class KotlinDataChannel<V>(
    private val delegate: Channel<V> = Channel()
) : DataChannel<V> {
    override suspend fun put(value: V) {
        delegate.send(value)
    }

    override suspend fun get(value: V) = delegate.receive()

    override suspend fun forEach(action: suspend (V) -> Unit) {
        for (value in delegate) {
            action(value)
        }
    }
}