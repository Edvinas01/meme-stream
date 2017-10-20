package com.edd.memestream

import java.util.*
import kotlin.reflect.KClass

class MemoryMemeQueue : MemeQueue {

    private val memes = mutableMapOf<KClass<out Meme>, Queue<Meme>>()

    override fun <T : Meme> add(meme: T): T {
        synchronized(this) {
            var queue = memes[meme::class]
            if (queue == null) {
                queue = LinkedList<Meme>()
                memes.put(meme::class, queue)
            }

            queue.offer(meme)
            return meme
        }
    }

    override fun <T : Meme> poll(clazz: KClass<T>): T? {
        synchronized(this) {
            return memes[clazz]?.let {
                val meme = it.poll()

                if (it.isEmpty()) {
                    memes.remove(clazz)
                }

                if (meme == null) {
                    null
                } else {
                    meme as T
                }
            }
        }
    }
}