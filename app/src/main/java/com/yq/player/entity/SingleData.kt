package com.yq.invitationcat.model

open class SingleData<T> : MutableMap<String, T> {

    protected val _kv by lazy { mutableMapOf<String, T>() }

    override val size: Int
        get() = _kv.size

    override fun containsKey(key: String): Boolean = _kv.containsKey(key)

    override fun containsValue(value: T): Boolean = _kv.containsValue(value)
    override fun get(key: String): T? {
        return _kv[key]
    }

    override fun isEmpty(): Boolean = _kv.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<String, T>>
        get() = _kv.entries
    override val keys: MutableSet<String>
        get() = _kv.keys
    override val values: MutableCollection<T>
        get() = _kv.values

    override fun clear() {
        _kv.clear()
    }

    override fun put(key: String, value: T): T? {
        name = key
        return _kv.put(key, value)
    }

    override fun putAll(from: Map<out String, T>) {
        _kv.putAll(from)
    }

    override fun remove(key: String): T? = _kv.remove(key)

    var name: String? = null
    val value: T?
        get() = get(name)

    override fun toString(): String {
        return _kv.toString()
    }

}