package com.edd.memestream.storage

class LocalStorageRepository<T>(private val storage: MutableMap<String, T>) {

    operator fun get(key: String) = storage[key]

    operator fun set(key: String, value: T) {
        storage.put(key, value)
    }
}