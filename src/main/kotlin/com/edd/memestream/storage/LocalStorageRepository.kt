package com.edd.memestream.storage

import org.mapdb.DB

class LocalStorageRepository<T>(
        private val storage: MutableMap<String, T>,
        private val db: DB
) {

    /**
     * Get locally stored value by key.
     */
    operator fun get(key: String) = storage[key]

    /**
     * Rewrite locally stored value.
     */
    operator fun set(key: String, value: T) {
        storage.put(key, value)
        db.commit()
    }
}