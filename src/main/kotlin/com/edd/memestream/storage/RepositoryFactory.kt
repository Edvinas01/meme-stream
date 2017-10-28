package com.edd.memestream.storage

import com.edd.memestream.config.Config
import com.edd.memestream.config.DbProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.Serializer
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

class RepositoryFactory {

    companion object {
        private val LOG = LoggerFactory.getLogger(RepositoryFactory::class.java)
        private const val SIMPLE_REPOSITORY = "simple-memes"
    }

    private val mapper = ObjectMapper().registerModule(KotlinModule())
    private val db = createDb()

    /**
     * Factorize a simple repository.
     */
    fun simple(): Repository<String, SimpleMeme> {
        return Repository(db
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
        val config = Config[DbProperties::class.java]
        if (config == null) {
            LOG.error("DB configuration is missing")
            exitProcess(-1)
        }
        return DBMaker
                .fileDB(config.path)
                .closeOnJvmShutdown()
                .make()
    }
}