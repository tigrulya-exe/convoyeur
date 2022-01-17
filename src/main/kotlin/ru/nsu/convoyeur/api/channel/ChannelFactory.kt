package ru.nsu.convoyeur.api.channel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS

interface ChannelFactory {
    companion object {
        const val DEFAULT_BUFF_SIZE = 1
    }

    fun <V> createChannel(buffSize: Int = RENDEZVOUS): Channel<V>
}