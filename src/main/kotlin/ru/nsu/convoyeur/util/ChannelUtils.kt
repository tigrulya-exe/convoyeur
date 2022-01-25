package ru.nsu.convoyeur.util

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.selects.select

suspend fun <E> getFirstChannelValue(channels: Map<String, ReceiveChannel<E>>) = select<Pair<String, E?>> {
    channels.forEach {
        it.value.onReceiveCatching { result ->
            it.key to result.getOrNull()
        }
    }
}