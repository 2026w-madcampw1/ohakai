package com.example.constellationapp.network

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiResponse(
    val choices: List<Choice>,
)

@Serializable
data class Choice(
    val message: ResponseMessage,
)

@Serializable
data class ResponseMessage(
    val role: String,
    val content: String,
)
