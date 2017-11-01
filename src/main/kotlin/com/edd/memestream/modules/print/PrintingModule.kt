package com.edd.memestream.modules.print

import com.edd.memestream.config.Config
import com.edd.memestream.executors.Executor
import com.edd.memestream.modules.StatefulModule
import com.edd.memestream.modules.twitter.TwitterModule
import com.edd.memestream.storage.RepositoryManager
import org.slf4j.LoggerFactory

class PrintingModule(
        private val executor: Executor,
        repositories: RepositoryManager
) : StatefulModule<PrintingState>(
        repositories,
        PrintingState::class.java
) {

    private companion object {
        private val log = LoggerFactory.getLogger(TwitterModule::class.java)

        init {
            Config.register("printing", PrintingProperties::class.java)
        }
    }

    private val memeRepository = repositories.simple

    override fun start() {
        Config.getModule(PrintingProperties::class.java)?.apply {
            executor.schedule(Runnable {
                val state = getState() ?: PrintingState(0)
                val meme = memeRepository[state.offset]

                if (meme != null) {
                    setState(state.copy(offset = state.offset + 1))
                    log.debug("Pulled: $meme, state: $state")
                }
            }, intervalMillis)
        }
    }
}