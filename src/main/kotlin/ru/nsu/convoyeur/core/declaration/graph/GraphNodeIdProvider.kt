package ru.nsu.convoyeur.core.declaration.graph

import java.util.*

object GraphNodeIdProvider {
    fun provideId(): String = UUID.randomUUID().toString()
}