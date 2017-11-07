package com.edd.memestream.modules.api

import com.edd.memestream.executors.Executor

interface ExecutorAware {

    /**
     * Inject executor manager.
     */
    fun initExecutor(executor: Executor)
}