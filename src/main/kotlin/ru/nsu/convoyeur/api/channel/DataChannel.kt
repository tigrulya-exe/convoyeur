package ru.nsu.convoyeur.api.channel

//TODO тестовый апи, чтобы просто скомпилилось
interface DataChannel<V> {
    suspend fun put(value: V)

    suspend fun get(value: V): V

    suspend fun forEach(action: suspend (V) -> Unit)
}