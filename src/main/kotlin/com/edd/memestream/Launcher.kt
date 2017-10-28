package com.edd.memestream

import com.edd.memestream.config.Config
import com.edd.memestream.config.TwitterProperties
import com.edd.memestream.executors.Scheduler
import com.edd.memestream.storage.RepositoryFactory
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

fun main(args: Array<String>) {
    val repositories = RepositoryFactory()
    val scheduler = Scheduler()

    Config[TwitterProperties::class.java]?.apply {

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
}