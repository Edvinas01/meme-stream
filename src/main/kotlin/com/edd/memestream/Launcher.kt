package com.edd.memestream

import com.edd.memestream.executors.Executor
import com.edd.memestream.modules.print.PrintingModule
import com.edd.memestream.modules.twitter.TwitterModule
import com.edd.memestream.storage.RepositoryManager

fun main(args: Array<String>) {
    val repositories = RepositoryManager()
    val executor = Executor()

    listOf(
            PrintingModule(executor, repositories),
            TwitterModule(executor, repositories)
    ).forEach({
        it.start()
    })
}