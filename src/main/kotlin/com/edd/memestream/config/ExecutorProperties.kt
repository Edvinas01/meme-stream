package com.edd.memestream.config

data class ExecutorProperties(
        val producers: ExecutorPropertyGroup,
        val consumers: ExecutorPropertyGroup
) : Properties

data class ExecutorPropertyGroup(
        val poolSize: Int
)