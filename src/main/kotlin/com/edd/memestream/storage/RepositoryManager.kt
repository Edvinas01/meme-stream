package com.edd.memestream.storage

import com.edd.memestream.config.Config
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.Serializer

class RepositoryManager {

    private companion object {
        const val SIMPLE_REPOSITORY = "simple-memes"
        const val LOCAL_PREFIX = "local-"
    }

    private val mapper = ObjectMapper().registerModule(KotlinModule())
    private val db = createDb()

    val simple = simple()

    /**
     * Factorize a local storage repository.
     */
    fun <T> local(moduleType: Class<*>, valueType: Class<T>): LocalStorageRepository<T> {
        return LocalStorageRepository(db
                .hashMap(LOCAL_PREFIX + moduleType.name)
                .valueSerializer(JsonSerializer(mapper, valueType))
                .keySerializer(Serializer.STRING)
                .createOrOpen()
        )
    }

    /**
     * Factorize a simple repository.
     */
    private fun simple(): SimpleMemeRepository {
        return SimpleMemeRepository(db
                .indexTreeList(SIMPLE_REPOSITORY, JsonSerializer(mapper, SimpleMeme::class.java))
                .createOrOpen()
        )
    }

    /**
     * Create a new map db instance.
     */
    private fun createDb(): DB {
        return DBMaker
                .fileDB(Config.db.path)
                .closeOnJvmShutdown()
                .make()
    }
}