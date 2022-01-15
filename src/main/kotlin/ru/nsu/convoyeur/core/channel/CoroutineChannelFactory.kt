package ru.nsu.convoyeur.core.channel

import kotlinx.coroutines.channels.Channel
import ru.nsu.convoyeur.api.channel.ChannelFactory

class CoroutineChannelFactory : ChannelFactory {
    override fun <V> createChannel(buffSize: Int) = Channel<V>(capacity = buffSize)
}