package com.edd.memestream.modules.api

import com.edd.memestream.storage.LocalStorageRepository
import com.edd.memestream.storage.RepositoryManager

abstract class StatefulModule<T>(private val stateType: Class<T>) : Module, RepositoryAware {

    private companion object {
        const val STATE = "state"
    }

    private lateinit var localRepository: LocalStorageRepository<T>

    /**
     * Get currently stored local state.
     */
    protected fun getState(): T? {
        return localRepository[STATE]
    }

    /**
     * Replace currently stored state with a new one.
     */
    protected fun setState(state: T) {
        localRepository[STATE] = state
    }

    override fun initRepositories(repositoryManager: RepositoryManager) {
        localRepository = repositoryManager.local(this::class.java.name, stateType)
    }
}