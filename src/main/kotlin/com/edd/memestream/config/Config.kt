package com.edd.memestream.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import mu.KLogging
import java.io.File
import kotlin.system.exitProcess

object Config : KLogging() {

    private const val DEFAULT_CONFIG = "default-config.yml"
    private const val MODULES = "modules"
    private const val CONFIG = "config.yml"

    private val mapper = ObjectMapper(YAMLFactory())
            .registerModule(KotlinModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val moduleTree: JsonNode
    private val modules: MutableMap<Class<*>, Any> = mutableMapOf()

    val executors: ExecutorProperties
    val db: DbProperties

    init {
        val tree = mapper.readTree(loadBytes())
        moduleTree = tree.path(MODULES)

        val root = read(tree, RootProperties::class.java) ?: exitProcess(-1)
        executors = root.executors
        db = root.db
    }

    /**
     * Register a new module configuration.
     */
    fun <T> register(name: String, type: Class<T>): T? {
        val module = read(moduleTree.path(name), type, "$MODULES.$name.")
        if (module != null) {
            modules.put(type, module)
        } else {
            logger.debug { "Module \"$name\" was not loaded" }
        }
        return module
    }

    /**
     * Get module configuration.
     */
    fun <T> getModule(type: Class<T>): T? {
        return modules[type]?.let {
            if (type.isAssignableFrom(it.javaClass)) {
                type.cast(it)
            } else {
                null
            }
        }
    }

    /**
     * Load provided configuration type and handle all exceptions.
     */
    private fun <T> read(tree: JsonNode, type: Class<T>, prefix: String = ""): T? {
        try {
            return mapper.treeToValue(tree, type)
        } catch (e: MissingKotlinParameterException) {
            logger.error { "Required parameter [${prefix + getPath(e)}] in $CONFIG file is not set" }
        } catch (e: MismatchedInputException) {
            logger.error { "Parameter [${prefix + getPath(e)}] is incorrect type, expected: ${e.targetType}" }
        }
        return null
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

    /**
     * Load configuration file content as bytes.
     */
    private fun loadBytes(): ByteArray {
        val config = File(CONFIG)

        val bytes = if (!config.exists()) {
            logger.debug { "$CONFIG does not exist, creating default config file" }

            getDefaultBytes().let { b ->
                config.createNewFile()
                config.writeBytes(b)
                b
            }
        } else {
            config.readBytes()
        }

        return if (bytes.isEmpty()) {
            logger.debug { "$CONFIG is empty, copying default config file contents" }

            getDefaultBytes().let { b ->
                config.writeBytes(b)
                b
            }
        } else {
            bytes
        }
    }
}