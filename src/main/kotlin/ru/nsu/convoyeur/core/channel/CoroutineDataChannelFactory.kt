package ru.nsu.convoyeur.core.channel

import ru.nsu.convoyeur.api.channel.DataChannel
import ru.nsu.convoyeur.api.channel.DataChannelFactory

class CoroutineDataChannelFactory : DataChannelFactory {
    override fun <V> createChannel(): DataChannel<V> {
        return CoroutineDataChannel()
    }
}