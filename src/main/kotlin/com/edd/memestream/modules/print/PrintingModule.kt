package com.edd.memestream.modules.print

import com.edd.memestream.config.Config
import com.edd.memestream.executors.Executor
import com.edd.memestream.modules.Module
import com.edd.memestream.modules.twitter.TwitterModule
import com.edd.memestream.storage.RepositoryManager
import org.slf4j.LoggerFactory

class PrintingModule(
        private val executor: Executor,
        repositories: RepositoryManager
) : Module {

    private companion object {
        const val STATE = "state"
        const val NAME = "printing"

        private val log = LoggerFactory.getLogger(TwitterModule::class.java)

        init {
            Config.register(NAME, PrintingProperties::class.java)
        }
    }

    private val stateRepository = repositories.local(this::class.java, PrintingState::class.java)
    private val memeRepository = repositories.simple

    override fun start() {
        Config.getModule(PrintingProperties::class.java)?.apply {
            executor.schedule(Runnable {
                val state = stateRepository[STATE] ?: PrintingState(0)
                val meme = memeRepository[state.offset]

                if (meme != null) {
                    stateRepository[STATE] = state.copy(offset = state.offset + 1)
                    log.debug("Pulled: $meme, state: $state")
                }
            }, intervalMillis)
        }
    }
}