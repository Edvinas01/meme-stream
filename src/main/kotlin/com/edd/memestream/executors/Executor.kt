package com.edd.memestream.executors

import com.edd.memestream.config.Config
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Wrapper class for java executors.
 */
class Executor {

    private companion object {
        const val THREAD_NAMES = "meme-stream-scheduler"
    }

    private val executor: ScheduledExecutorService = Executors
            .newScheduledThreadPool(Config.executors.poolSize) { r -> Thread(r, THREAD_NAMES) }

    /**
     * Schedule a job which will run at a fixed rate using the provided interval.
     */
    fun schedule(runnable: Runnable, intervalMillis: Long, delayMillis: Long = 0) {
        executor.scheduleAtFixedRate(runnable, delayMillis, intervalMillis, TimeUnit.MILLISECONDS)
    }

    /**
     * Run a runnable in async.
     */
    fun run(runnable: Runnable) {
        executor.submit(runnable)
    }
}