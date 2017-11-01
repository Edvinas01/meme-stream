package com.edd.memestream.modules.twitter

import com.edd.memestream.config.Config
import com.edd.memestream.executors.Executor
import com.edd.memestream.modules.StatefulModule
import com.edd.memestream.storage.RepositoryManager
import com.edd.memestream.storage.SimpleMeme
import org.slf4j.LoggerFactory
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

class TwitterModule(
        private val executor: Executor,
        repositories: RepositoryManager
) : StatefulModule<TwitterState>(
        repositories,
        TwitterState::class.java
) {

    private companion object {
        private val log = LoggerFactory.getLogger(TwitterModule::class.java)

        init {
            Config.register("twitter", TwitterProperties::class.java)
        }
    }

    private val memeRepository = repositories.simple

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
                        mediaEntities.map {
                            SimpleMeme(it.mediaURL)
                        }.firstOrNull()?.let { meme ->

                            if (state.previousUserMeme[user] != meme) {
                                state.previousUserMeme[user] = meme
                                pending += meme
                            }
                        }
                    }
                }

                setState(state)

                if (pending.isNotEmpty()) {
                    log.debug("Storing $pending")
                }
                memeRepository += pending
            }, intervalMillis)
        }
    }
}