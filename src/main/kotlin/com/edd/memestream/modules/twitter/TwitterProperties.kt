package com.edd.memestream.modules.twitter

data class TwitterProperties(
        val auth: Auth,
        val intervalMillis: Long,
        val users: List<String>
)

data class Auth(
        val oauthAccessTokenSecret: String,
        val oauthAccessToken: String,
        val oauthConsumerSecret: String,
        val oauthConsumerKey: String
) {

    override fun toString(): String {
        return "Auth(Protected)"
    }
}