package com.edd.memestream.modules.api

import com.edd.memestream.storage.RepositoryManager

interface RepositoryAware {

    /**
     * Inject repository manager.
     */
    fun initRepositories(repositoryManager: RepositoryManager)
}