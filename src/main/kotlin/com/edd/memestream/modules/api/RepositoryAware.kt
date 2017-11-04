package com.edd.memestream.modules.api

import com.edd.memestream.storage.RepositoryManager

interface RepositoryAware {

    /**
     * Initialize module repositories.
     */
    fun initRepositories(repositoryManager: RepositoryManager)
}