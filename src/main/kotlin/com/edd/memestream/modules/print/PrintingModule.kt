package com.edd.memestream.modules.print

import com.edd.memestream.config.Config
import com.edd.memestream.executors.Executor
import com.edd.memestream.modules.Module
import com.edd.memestream.storage.SimpleMemeRepository

class PrintingModule(
        private val repository: SimpleMemeRepository,
        private val executor: Executor
) : Module {

    private companion object {
        const val NAME = "printing"

        init {
            Config.register(NAME, PrintingProperties::class.java)
        }
    }

    override fun start() {
        Config.getModule(PrintingProperties::class.java)?.apply {
            executor.schedule(Runnable {
                repository.pollFirst { println(it) }
            }, intervalMillis)
        }
    }
}