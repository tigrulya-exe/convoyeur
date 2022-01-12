package ru.nsu.convoyeur.api.execution.channel

import ru.nsu.convoyeur.api.channel.DataChannel

interface DataChannelFactory {
    fun <V> createChannel(): DataChannel<V>
}