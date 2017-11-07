package com.edd.memestream

import com.edd.memestream.executors.Executor
import com.edd.memestream.modules.discord.DiscordModule
import com.edd.memestream.modules.print.PrintingModule
import com.edd.memestream.modules.twitter.TwitterModule
import com.edd.memestream.storage.RepositoryManager
import mu.KotlinLogging

fun main(args: Array<String>) {
    val logger = KotlinLogging.logger { }

    val repositories = RepositoryManager()
    val executor = Executor()

    listOf(PrintingModule(), TwitterModule(), DiscordModule()).forEach({
        try {
            it.initRepositories(repositories)
            it.initExecutor(executor)
            it.start()

        } catch (e: RuntimeException) {
            logger.error(e) { "Could not start ${it.javaClass.name}" }
        }
    })
}