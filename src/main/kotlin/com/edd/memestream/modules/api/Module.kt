package com.edd.memestream.modules.api

interface Module {

    /**+
     * Start the module. Called after module is fully initialized.
     */
    fun start()
}