package com.edd.memestream.config

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.exitProcess

object Config {

    private const val DEFAULT_CONFIG = "default-config.yml"
    private const val CONFIG = "config.yml"

    private val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())
    private val log = LoggerFactory.getLogger(Config::class.java)

    private val rootProperties: RootProperties

    init {
        rootProperties = loadAndHandle()
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Properties> get(type: Class<T>): T? {
        if (type == TwitterProperties::class.java) {
            return rootProperties.producers?.twitter as T
        }
        return null
    }

    /**
     * Load root properties and handle all exceptions.
     */
    private fun loadAndHandle(): RootProperties {
        try {
            return load()
        } catch (e: MissingKotlinParameterException) {
            log.error("Required parameter at ${getPath(e)} in $CONFIG file is not set")
        } catch (e: MismatchedInputException) {
            log.error("Parameter at ${getPath(e)} is incorrect type, expected: ${e.targetType}")
        }
        throw exitProcess(-1)
    }

    /**
     * Load root properties from external file or default config.
     */
    private fun load(): RootProperties {
        val config = File(CONFIG)

        if (!config.exists()) {
            log.debug("$CONFIG does not exist, creating default config file")

            return getDefaultBytes().let { b ->
                config.createNewFile()
                config.writeBytes(b)
                load(b)
            }
        }

        val bytes = config.readBytes()
        if (bytes.isEmpty()) {
            log.debug("$CONFIG is empty, copying default config file contents")

            return getDefaultBytes().let { b ->
                config.writeBytes(b)
                load(b)
            }
        }
        return load(bytes)
    }

    /**
     * Load root properties from byte array.
     */
    private fun load(bytes: ByteArray): RootProperties {
        return mapper.readValue<RootProperties>(
                bytes,
                RootProperties::class.java
        )
    }

    /**
     * Get default config file bytes.
     */
    private fun getDefaultBytes() =
            ClassLoader.getSystemResource(DEFAULT_CONFIG).readBytes()

    /**
     * Get problematic param path from json exception.
     */
    private fun getPath(e: JsonMappingException) = e.path.joinToString(".") { it.fieldName }
}

/**
 * Wrapper class for all properties.
 */
private data class RootProperties(
        val producers: Producers?
)

/**
 * Wrapper class for all producers.
 */
private data class Producers(
        val twitter: TwitterProperties?
)