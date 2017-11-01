package com.edd.memestream.modules

import com.edd.memestream.storage.LocalStorageRepository
import com.edd.memestream.storage.RepositoryManager

abstract class StatefulModule<T>(
        repositoryManager: RepositoryManager,
        valueType: Class<T>
) : Module {

    private companion object {
        const val STATE = "state"
    }

    @Suppress("LeakingThis")
    private val localRepository: LocalStorageRepository<T> =
            repositoryManager.local(this::class.java.name, valueType)

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
}