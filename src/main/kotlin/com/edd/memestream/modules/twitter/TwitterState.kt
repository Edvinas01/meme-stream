package com.edd.memestream.modules.twitter

import com.edd.memestream.storage.SimpleMeme

data class TwitterState(val previousUserMeme: MutableMap<String, SimpleMeme>)