package com.edd.memestream.modules.discord

data class DiscordProperties(
        val intervalMillis: Long,
        val auth: Auth,
        val channels: Set<String>
)

data class Auth(val token: String) {

    override fun toString(): String {
        return "Auth(Protected)"
    }
}