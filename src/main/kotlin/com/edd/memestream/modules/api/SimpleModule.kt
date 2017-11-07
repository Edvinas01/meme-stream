package com.edd.memestream.modules.api

import com.edd.memestream.executors.Executor
import com.edd.memestream.storage.RepositoryManager
import com.edd.memestream.storage.SimpleMemeRepository

abstract class SimpleModule<T>(stateType: Class<T>) : StatefulModule<T>(stateType), ExecutorAware {

    protected lateinit var simpleMemeRepository: SimpleMemeRepository
        private set

    protected lateinit var executor: Executor
        private set

    override fun initRepositories(repositoryManager: RepositoryManager) {
        super.initRepositories(repositoryManager)
        simpleMemeRepository = repositoryManager.simple
    }

    override fun initExecutor(executor: Executor) {
        this.executor = executor
    }
}