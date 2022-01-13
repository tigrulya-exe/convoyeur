package ru.nsu.convoyeur.api.channel

interface DataChannelFactory {
    fun <V> createChannel(): DataChannel<V>
}