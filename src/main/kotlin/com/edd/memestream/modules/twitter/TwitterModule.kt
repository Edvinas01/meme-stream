package com.edd.memestream.modules.twitter

import com.edd.memestream.config.Config
import com.edd.memestream.modules.api.SimpleModule
import com.edd.memestream.storage.SimpleMeme
import mu.KLogging
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

class TwitterModule : SimpleModule<TwitterState>(TwitterState::class.java) {

    private companion object : KLogging() {
        init {
            Config.register("twitter", TwitterProperties::class.java)
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
                val state = getState() ?: TwitterState(mutableMapOf())
                val pending = mutableListOf<SimpleMeme>()

                for (user in users) {
                    with(twitter.showUser(user).status) {
                        val url = mediaEntities.map {
                            it.expandedURL
                        }.firstOrNull()

                        val meme = if (url == null) {
                            SimpleMeme("https://twitter.com/$user/status/$id")
                        } else {
                            SimpleMeme(url)
                        }

                        if (state.previousUserMeme[user] != meme) {
                            state.previousUserMeme[user] = meme
                            pending += meme
                        }
                    }
                }

                setState(state)

                if (pending.isNotEmpty()) {
                    logger.debug { "Storing $pending" }
                }
                simpleMemeRepository += pending
            }, intervalMillis)
        }
    }
}