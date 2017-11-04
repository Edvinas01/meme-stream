package com.edd.memestream.modules.api

import com.edd.memestream.storage.RepositoryManager
import com.edd.memestream.storage.SimpleMemeRepository

abstract class SimpleModule<T>(stateType: Class<T>) : StatefulModule<T>(stateType) {

    protected lateinit var simpleMemeRepository: SimpleMemeRepository
        private set

    override fun initRepositories(repositoryManager: RepositoryManager) {
        super.initRepositories(repositoryManager)
        simpleMemeRepository = repositoryManager.simple
    }
}