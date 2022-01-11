package ru.nsu.convoyeur.api.channel.key

data class StringChannelKey(val name: String) : ChannelKey

fun String.asChannelKey() = StringChannelKey(this)