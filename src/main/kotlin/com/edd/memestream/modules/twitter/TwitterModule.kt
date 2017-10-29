package com.edd.memestream.modules.twitter

import com.edd.memestream.config.Config
import com.edd.memestream.executors.Executor
import com.edd.memestream.modules.Module
import com.edd.memestream.storage.SimpleMeme
import com.edd.memestream.storage.SimpleMemeRepository
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

class TwitterModule(
        private val repository: SimpleMemeRepository,
        private val executor: Executor
) : Module {

    private companion object {
        const val NAME = "twitter"

        init {
            Config.register(NAME, TwitterProperties::class.java)
        }
    }

    override fun start() {
        Config.getModule(TwitterProperties::class.java)?.apply {
            val twitter = TwitterFactory(ConfigurationBuilder()
                    .setOAuthAccessTokenSecret(auth.oauthAccessTokenSecret)
                    .setOAuthAccessToken(auth.oauthAccessToken)
                    .setOAuthConsumerSecret(auth.oauthConsumerSecret)
                    .setOAuthConsumerKey(auth.oauthConsumerKey)
                    .build()
            ).instance

            executor.schedule(Runnable {
                for (user in users) {
                    executor.run({
                        with(twitter.showUser(user).status) {
                            repository.save(id.toString(), SimpleMeme(text))
                        }
                    })
                }
            }, intervalMillis)
        }
    }
}