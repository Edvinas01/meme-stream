package com.edd.memestream.executors

import com.edd.memestream.config.Config
import com.edd.memestream.config.ExecutorProperties
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class Scheduler {

    private val producerExecutor: ScheduledExecutorService
    private val consumerExecutor: ScheduledExecutorService

    init {
        val props = Config[ExecutorProperties::class.java]!!

        producerExecutor = Executors.newScheduledThreadPool(props.producers.poolSize)
        consumerExecutor = Executors.newScheduledThreadPool(props.producers.poolSize)
    }

    /**
     * Schedule a job using the consumer executor.
     */
    fun consume(runnable: Runnable, intervalMillis: Long, delayMillis: Long = 0) {
        producerExecutor.scheduleAtFixedRate(runnable, delayMillis, intervalMillis, TimeUnit.MILLISECONDS)
    }

    /**
     * Schedule a job using the producer executor.
     */
    fun produce(runnable: Runnable, intervalMillis: Long, delayMillis: Long = 0) {
        consumerExecutor.scheduleAtFixedRate(runnable, delayMillis, intervalMillis, TimeUnit.MILLISECONDS)
    }
}