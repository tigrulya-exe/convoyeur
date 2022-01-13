package ru.nsu.convoyeur.core.execution.context

import ru.nsu.convoyeur.api.channel.DataChannel
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext

class NodeExecutionContext<S, D>(
    override var inputChannels: MutableMap<String, DataChannel<S>> = mutableMapOf(),
    override var outputChannels: MutableMap<String, DataChannel<D>> = mutableMapOf(),
) : MutableExecutionContext<S, D>