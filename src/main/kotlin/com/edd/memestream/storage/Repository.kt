package com.edd.memestream.storage

import org.mapdb.BTreeMap

class Repository<in K, V>(private val map: BTreeMap<K, V>) {

    /**
     * Save a single object.
     */
    fun save(key: K, value: V) {
        map[key] = value
    }

    /**
     * Poll and remove the first stored object.
     *
     * @param consumer consumer used for batch value processing.
     */
    fun pollFirst(consumer: (V) -> Unit = {}): V? {
        synchronized(this) {
            val entry = map.entries.firstOrNull() ?: return null
            val value = entry.value

            // Value should never be null at this point.
            consumer(value!!)
            map.remove(entry.key)

            return value
        }
    }
}