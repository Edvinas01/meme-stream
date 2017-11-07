package com.edd.memestream.modules.discord

import com.edd.memestream.config.Config
import com.edd.memestream.modules.api.SimpleModule
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.util.MessageBuilder

class DiscordModule : SimpleModule<DiscordState>(DiscordState::class.java) {

    init {
        Config.register("discord", DiscordProperties::class.java)
    }

    override fun start() {
        Config.getModule(DiscordProperties::class.java)?.apply {

            val client = ClientBuilder()
                    .withToken(auth.token)
                    .login()

            executor.schedule(Runnable {
                val state = getState() ?: DiscordState(0)
                val meme = simpleMemeRepository[state.offset]

                if (meme != null) {
                    client.channels.filter {
                        channels.contains(it.name)
                    }.map {
                        it.longID
                    }.forEach { id ->
                        MessageBuilder(client)
                                .withChannel(id)
                                .withContent(meme.content)
                                .send()
                    }
                    setState(state.copy(offset = state.offset + 1))
                }
            }, intervalMillis)
        }
    }
}