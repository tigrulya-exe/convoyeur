package ru.nsu.convoyeur.util

fun <E> LinkedHashSet<E>.pop(): E {
    val iterator = iterator()
    return iterator.next().also {
        iterator.remove()
    }
}

fun <K, V> MutableMap<K, V>.withDefaultCompute(defaultValue: (key: K) -> V): MutableMap<K, V> =
    MutableMapWithDefaultCompute(this, defaultValue)

class MutableMapWithDefaultCompute<K, V>(
    private val map: MutableMap<K, V>,
    private val default: (key: K) -> V
) : MutableMap<K, V> {
    override fun equals(other: Any?): Boolean = map == other
    override fun hashCode(): Int = map.hashCode()
    override fun toString(): String = map.toString()
    override val size: Int get() = map.size
    override fun isEmpty(): Boolean = map.isEmpty()
    override fun containsKey(key: K): Boolean = map.containsKey(key)
    override fun containsValue(value: @UnsafeVariance V): Boolean = map.containsValue(value)
    override fun get(key: K): V? = map.computeIfAbsent(key, default)
    override val keys: MutableSet<K> get() = map.keys
    override val values: MutableCollection<V> get() = map.values
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() = map.entries

    override fun put(key: K, value: V): V? = map.put(key, value)
    override fun remove(key: K): V? = map.remove(key)
    override fun putAll(from: Map<out K, V>) = map.putAll(from)
    override fun clear() = map.clear()
}
