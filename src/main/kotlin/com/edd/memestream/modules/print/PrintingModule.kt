package com.edd.memestream.modules.print

import com.edd.memestream.config.Config
import com.edd.memestream.modules.api.SimpleModule
import mu.KLogging

class PrintingModule : SimpleModule<PrintingState>(PrintingState::class.java) {

    private companion object: KLogging() {
        init {
            Config.register("printing", PrintingProperties::class.java)
        }
    }

    override fun start() {
        Config.getModule(PrintingProperties::class.java)?.apply {
            executor.schedule(Runnable {
                val state = getState() ?: PrintingState(0)
                val meme = simpleMemeRepository[state.offset]

                if (meme != null) {
                    setState(state.copy(offset = state.offset + 1))
                    logger.debug { "Pulled: $meme, state: $state" }
                }
            }, intervalMillis)
        }
    }
}