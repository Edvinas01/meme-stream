package com.edd.memestream.modules.twitter

import com.edd.memestream.config.Config
import com.edd.memestream.executors.Executor
import com.edd.memestream.modules.Module
import com.edd.memestream.storage.RepositoryManager
import com.edd.memestream.storage.SimpleMeme
import org.slf4j.LoggerFactory
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

class TwitterModule(
        private val executor: Executor,
        repositories: RepositoryManager
) : Module {

    private companion object {
        const val STATE = "state"
        const val NAME = "twitter"

        private val log = LoggerFactory.getLogger(TwitterModule::class.java)

        init {
            Config.register(NAME, TwitterProperties::class.java)
        }
    }

    private val stateRepository = repositories.local(this::class.java, TwitterState::class.java)
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
                val state = stateRepository[STATE] ?: TwitterState(mutableSetOf())

                for (user in users) {
                    with(twitter.showUser(user).status) {
                        val meme = SimpleMeme(text)
                        if (!state.previous.contains(meme)) {
                            log.debug("Storing $meme")

                            memeRepository += meme
                            state.previous.add(meme)
                        } else {
                            log.debug("$meme was stored previously")
                        }
                    }
                }
                stateRepository[STATE] = state
            }, intervalMillis)
        }
    }
}