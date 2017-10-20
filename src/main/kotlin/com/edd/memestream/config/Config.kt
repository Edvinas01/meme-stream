package com.edd.memestream.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule

object Config {

    private val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())
    private val rootProperties = mapper.readValue<RootProperties>(
            ClassLoader.getSystemResourceAsStream("default-config.yml"),
            RootProperties::class.java
    )

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Properties> get(type: Class<T>): T? {
        if (type == TwitterProperties::class.java) {
            return rootProperties.producers?.twitter as T
        }
        return null
    }
}

private data class RootProperties(
        val producers: Producers?
)

private data class Producers(
        val twitter: TwitterProperties?
)