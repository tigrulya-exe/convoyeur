package ru.nsu.convoyeur.api.execution.channel

import ru.nsu.convoyeur.api.channel.DataChannel
import ru.nsu.convoyeur.api.channel.KotlinDataChannel

class KotlinChannelFactory : DataChannelFactory {
    override fun <V> createChannel(): DataChannel<V> {
        return KotlinDataChannel()
    }
}