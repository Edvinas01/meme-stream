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

    private val root: RootProperties

    init {
        root = loadAndHandle()
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Properties> get(type: Class<T>): T? {
        return when (type) {
            ExecutorProperties::class.java -> root.executors as T
            TwitterProperties::class.java -> root.modules?.twitter as T
            DbProperties::class.java -> root.db as T
            else -> null
        }
    }

    /**
     * Load root properties and handle all exceptions.
     */
    private fun loadAndHandle(): RootProperties {
        var props: RootProperties? = null
        try {
            props = load()
        } catch (e: MissingKotlinParameterException) {
            log.error("Required parameter at ${getPath(e)} in $CONFIG file is not set")
        } catch (e: MismatchedInputException) {
            log.error("Parameter at ${getPath(e)} is incorrect type, expected: ${e.targetType}")
        }
        return props ?: exitProcess(-1)
    }

    /**
     * Load root properties from external file or default config.
     */
    private fun load(): RootProperties? {
        val config = File(CONFIG)

        if (!config.exists()) {
            log.debug("$CONFIG does not exist, creating default config file")

            return getDefaultBytes().let { b ->
                config.createNewFile()
                config.writeBytes(b)
                null
            }
        }

        val bytes = config.readBytes()
        if (bytes.isEmpty()) {
            log.debug("$CONFIG is empty, copying default config file contents")

            return getDefaultBytes().let { b ->
                config.writeBytes(b)
                null
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
        val executors: ExecutorProperties,
        val modules: ModuleProperties?,
        val db: DbProperties
)

/**
 * Wrapper class for all modules.
 */
private data class ModuleProperties(
        val twitter: TwitterProperties?
)