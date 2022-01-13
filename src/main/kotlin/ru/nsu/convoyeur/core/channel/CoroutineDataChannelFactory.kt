package ru.nsu.convoyeur.core.channel

import kotlinx.coroutines.channels.Channel
import ru.nsu.convoyeur.api.channel.DataChannel
import ru.nsu.convoyeur.api.channel.DataChannelFactory

class CoroutineDataChannelFactory : DataChannelFactory {
    override fun <V> createChannel(buffSize: Int): DataChannel<V> {
        return CoroutineDataChannel(
            Channel(capacity = buffSize)
        )
    }
}