package com.edd.memestream

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {

    val repository = MemoryMemeQueue()
    val executor = Executors.newScheduledThreadPool(6)

    // Producers.
    val imageProducer = Runnable {
        repository.add(RemoteImageMeme("Image"))
    }

    val textProducer = Runnable {
        repository.add(TextMeme("Text"))
    }

    executor.scheduleAtFixedRate(imageProducer, 0, 10, TimeUnit.SECONDS)
    executor.scheduleAtFixedRate(textProducer, 0, 10, TimeUnit.SECONDS)

    // Consumers.
    val imageConsumer = Runnable {
        repository.poll(RemoteImageMeme::class)?.let {
            println(it)
        }
    }

    val textConsumer = Runnable {
        repository.poll(TextMeme::class)?.let {
            println(it)
        }
    }

    executor.scheduleAtFixedRate(imageConsumer, 0, 2, TimeUnit.SECONDS)
    executor.scheduleAtFixedRate(imageConsumer, 0, 2, TimeUnit.SECONDS)

    executor.scheduleAtFixedRate(textConsumer, 0, 2, TimeUnit.SECONDS)
    executor.scheduleAtFixedRate(textConsumer, 0, 2, TimeUnit.SECONDS)
}