package com.edd.memestream.storage

import org.mapdb.IndexTreeList

class SimpleMemeRepository(private val list: IndexTreeList<SimpleMeme>) {

    /**
     * Save a new simple meme.
     */
    operator fun plusAssign(meme: SimpleMeme) {
        list.add(meme)
    }

    /**
     * Get a simple meme by its id.
     */
    operator fun get(id: Number): SimpleMeme? {
        val idx = id.toInt()
        if (idx < 0) {
            return null
        }

        synchronized(list) {
            if (idx < list.size) {
                return list[idx]
            }
        }
        return null
    }
}