package com.edd.memestream.storage

import com.edd.memestream.config.Config
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.Serializer

class RepositoryFactory {

    private companion object {
        const val SIMPLE_REPOSITORY = "simple-memes"
    }

    private val mapper = ObjectMapper().registerModule(KotlinModule())
    private val db = createDb()

    /**
     * Factorize a simple repository.
     */
    fun simple(): SimpleMemeRepository {
        return SimpleMemeRepository(db
                .treeMap(SIMPLE_REPOSITORY)
                .valueSerializer(JsonGroupSerializer(mapper, SimpleMeme::class.java))
                .keySerializer(Serializer.STRING)
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