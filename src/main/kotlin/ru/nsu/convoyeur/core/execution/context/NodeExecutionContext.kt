package ru.nsu.convoyeur.core.execution.context

import kotlinx.coroutines.channels.Channel
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext

class NodeExecutionContext<S, D>(
    override var inputChannels: MutableMap<String, Channel<S>> = mutableMapOf(),
    override var outputChannels: MutableMap<String, Channel<D>> = mutableMapOf(),
) : MutableExecutionContext<S, D>