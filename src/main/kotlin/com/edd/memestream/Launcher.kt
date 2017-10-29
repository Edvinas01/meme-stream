package com.edd.memestream

import com.edd.memestream.executors.Executor
import com.edd.memestream.modules.print.PrintingModule
import com.edd.memestream.modules.twitter.TwitterModule
import com.edd.memestream.storage.RepositoryFactory

fun main(args: Array<String>) {
    val simpleRepository = RepositoryFactory().simple()
    val executor = Executor()

    listOf(
            PrintingModule(simpleRepository, executor),
            TwitterModule(simpleRepository, executor)
    ).forEach({
        it.start()
    })
}