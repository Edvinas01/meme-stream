package com.edd.memestream.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.yaml.snakeyaml.Yaml

object Config {

    private val properties = Yaml().load<Map<String, Any>>(ClassLoader.getSystemResourceAsStream("default-config.yml"))

    init {
        val tree = ObjectMapper(YAMLFactory()).readTree(ClassLoader.getSystemResourceAsStream("default-config.yml"))
        println()
    }

    operator fun <T : Properties> get(type: Class<T>): Pair<T?, String> {
        val path = Path.travel(properties)

        val producers = properties.getMap("producers")
                ?: return error("No producers configured")

        if (TwitterProducer::class.java == type) {
            return producers.getMap("twitter")?.let { twitter ->
                val auth = twitter.getMap("auth")
                        ?: return error("Authentication for twitter is not configured")

                val users = twitter.getList("users", String::class.java)
                        ?: return error("No users are configured")

                val result = TwitterProducer(
                        Auth(
                                oauthAccessTokenSecret = auth
                                        .getString("oauthAccessTokenSecret")
                                        ?: return error("oauthAccessTokenSecret is not set"),
                                oauthAccessToken = auth
                                        .getString("oauthAccessToken")
                                        ?: return error("oauthAccessToken is not set"),
                                oauthConsumerSecret = auth
                                        .getString("oauthConsumerSecret")
                                        ?: return error("oauthConsumerSecret is not set"),
                                oauthConsumerKey = auth
                                        .getString("oauthConsumerKey")
                                        ?: return error("oauthConsumerKey is not set")
                        ),
                        users
                )

                @Suppress("UNCHECKED_CAST")
                (result as T).to("Success")

            } ?: error("Module of type $type is not configured")
        }
        return error("Module of type $type is not supported")
    }

    private fun <T> error(message: String): Pair<T?, String> {
        return Pair(null, message)
    }

    private fun Map<String, Any>.getString(key: String): String? {
        val result = get(key)
        if (result is String) {
            return result
        }
        return null
    }

    private fun <T> Map<String, Any>.getList(key: String, type: Class<T>): List<T>? {
        val raw = get(key)
        if (raw is List<*>) {
            try {
                @Suppress("UNCHECKED_CAST")
                return raw as List<T>
            } catch (ignored: ClassCastException) {
            }
        }
        return null
    }

    private fun Map<String, Any>.getMap(key: String): Map<String, Any>? {
        val result = get(key)
        if (result is Map<*, *>) {

            @Suppress("UNCHECKED_CAST")
            return result as Map<String, Any>
        }
        return null
    }
}

/**
 * Helper class to parse raw maps.
 */
private class Path private constructor(
        map: Map<String, Any>,
        private val path: List<String>
) : HashMap<String, Any>(map) {

    companion object {
        fun travel(map: Map<String, Any>) = Path(map, listOf())
    }

    /**
     * Get next path based on its name.
     */
    fun path(key: String): Path {
        val value = get(key)
        val travel = path + key

        println(travel)
        if (value == null || value !is Map<*, *>) {
            return Path(emptyMap(), travel)
        }

        @Suppress("UNCHECKED_CAST")
        return Path(value as Map<String, Any>, travel)
    }

    /**
     * Get next value from current path.
     */
    inline fun <reified T> get(key: String, type: Class<T>): Pair<T?, String> {

        @Suppress("UNCHECKED_CAST")
        val value = (get(key)
                ?: return pathError(key, "is not set")) as? T
                ?: return pathError(key, "is not of type ${type.simpleName}")

        return Pair(value, "Success")
    }

    /**
     * Helper function to create a pair with an error message which includes current path info.
     */
    private fun <T> pathError(current: String, message: String): Pair<T?, String> {
        return Pair(null, "Value at \"${path.joinToString(".")}.$current\" $message")
    }
}