package com.edd.meme_stream

import kotlin.reflect.KClass

interface MemeQueue {

    fun <T : Meme> add(meme: T): T

    fun <T : Meme> poll(clazz: KClass<T>): T?
}