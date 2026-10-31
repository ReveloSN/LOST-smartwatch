package com.lost.domain.map

/**
 * Caché en memoria que olvida lo menos usado. `LinkedHashMap` con orden de acceso:
 * leer un elemento lo mueve al final y, al pasarse del tope, sale el primero (el más viejo).
 */
class LruCache<K, V>(private val maxEntries: Int) {
    private val entries = object : LinkedHashMap<K, V>(maxEntries, LOAD_FACTOR, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>): Boolean = size > maxEntries
    }

    val size: Int @Synchronized get() = entries.size

    @Synchronized
    operator fun get(key: K): V? = entries[key]

    @Synchronized
    operator fun set(key: K, value: V) {
        entries[key] = value
    }

    private companion object {
        const val LOAD_FACTOR = 0.75f
    }
}
