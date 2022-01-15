package ru.nsu.convoyeur.api.channel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS

interface ChannelFactory {
    fun <V> createChannel(buffSize: Int = RENDEZVOUS): Channel<V>
}