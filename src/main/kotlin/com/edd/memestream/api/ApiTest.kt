package com.edd.memestream.api

import com.edd.memestream.config.Config
import com.edd.memestream.config.TwitterProducer
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder


fun main(args: Array<String>) {
    val (config, message) = Config[TwitterProducer::class.java]
    if (config == null) {
        throw Exception("Could not get twitter config: $message")
    }

    val (auth, users) = config

    val twitter = TwitterFactory(ConfigurationBuilder()
            .setOAuthAccessTokenSecret(auth.oauthAccessTokenSecret)
            .setOAuthAccessToken(auth.oauthAccessToken)
            .setOAuthConsumerSecret(auth.oauthConsumerSecret)
            .setOAuthConsumerKey(auth.oauthConsumerKey)
            .build()
    ).instance

    for (user in users) {
        println(twitter.showUser(user).status.text)
    }
}