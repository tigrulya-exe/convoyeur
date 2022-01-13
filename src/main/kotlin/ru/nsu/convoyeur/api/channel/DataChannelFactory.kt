package ru.nsu.convoyeur.api.channel

import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS

interface DataChannelFactory {
    fun <V> createChannel(buffSize: Int = RENDEZVOUS): DataChannel<V>
}