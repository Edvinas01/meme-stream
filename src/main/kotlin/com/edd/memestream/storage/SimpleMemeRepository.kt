package com.edd.memestream.storage

import org.mapdb.DB
import org.mapdb.IndexTreeList

class SimpleMemeRepository(
        private val list: IndexTreeList<SimpleMeme>,
        private val db: DB
) {

    /**
     * Save a new simple meme.
     */
    operator fun plusAssign(meme: SimpleMeme) {
        list.add(meme)
        db.commit()
    }

    /**
     * Save a list of simple memes in bulk.
     */
    operator fun plusAssign(memes: List<SimpleMeme>) {
        if (memes.isEmpty()) {
            return
        }

        list.addAll(memes)
        db.commit()
    }

    /**
     * Get a simple meme by its id.
     */
    operator fun get(id: Number): SimpleMeme? {
        val idx = id.toInt()
        if (idx >= 0) {
            synchronized(list) {
                if (idx < list.size) {
                    return list[idx]
                }
            }
        }
        return null
    }
}