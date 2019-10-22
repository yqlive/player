package com.yq.live.rely

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Attribute<T>(private val default: T? = null) {

    private val expendAttributes by lazy {
        hashMapOf<Any?, MutableList<Pair<String, Any?>>>()
    }

    private fun attribute(thisRef: Any?, new: Pair<String, Any?>) {
        val target = expendAttributes[thisRef] ?: mutableListOf()
        if (!expendAttributes.contains(thisRef))
            expendAttributes[thisRef] = target
        target.takeIf {
            it.replaceFirst(new) { old ->
                old.first == new.first
            }
        } ?: target.add(new)
    }

    private fun attribute(thisRef: Any?, key: String): T =
        expendAttributes[thisRef]?.find { it.first == key }?.second as T

    private fun removeAttribute(thisRef: Any?, key: String) {
        expendAttributes[thisRef]?._removeIf {
            it.first == key
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return attribute(thisRef, property.name) ?: default as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (value != null)
            attribute(thisRef, property.name to value)
        else
            removeAttribute(thisRef, property.name)
    }

}

fun <T> weak(blo: () -> T) = WeakReference(blo())
fun <T> weak(value: T) = WeakReference(value)

class WeakReference<T>(value: T) : ReadWriteProperty<Any?, T> {
    private var weakRef = java.lang.ref.WeakReference(value)
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return weakRef.get() as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        weakRef = java.lang.ref.WeakReference(value)
    }
}

