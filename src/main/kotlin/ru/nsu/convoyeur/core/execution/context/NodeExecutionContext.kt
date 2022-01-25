package ru.nsu.convoyeur.core.execution.context

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import ru.nsu.convoyeur.api.execution.context.MutableExecutionContext
import java.util.concurrent.atomic.AtomicBoolean

data class NodeExecutionContext<S, D>(
    override var inputChannels: MutableMap<String, ReceiveChannel<S>> = mutableMapOf(),
    override var outputChannels: MutableMap<String, SendChannel<D>> = mutableMapOf(),
    override var parallelIndex: Int = 1
) : MutableExecutionContext<S, D> {
    private var isActiveAtomic: AtomicBoolean = AtomicBoolean()

    /**
     * Key - next cycle node id (output channel),
     * value - previous cycle node id (input channel)
     */
    override var inputCycleChannelIds: MutableMap<String, String> = mutableMapOf()

    override var isActive: Boolean
        get() = isActiveAtomic.get()
        set(value) = isActiveAtomic.set(value)
}