package com.edd.memestream.config

data class TwitterProducer(
        val auth: Auth,
        val users: List<String>
) : Properties

data class Auth(
        val oauthAccessTokenSecret: String,
        val oauthAccessToken: String,
        val oauthConsumerSecret: String,
        val oauthConsumerKey: String
)