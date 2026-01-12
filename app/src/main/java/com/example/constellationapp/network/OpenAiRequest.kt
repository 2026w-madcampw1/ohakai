package com.example.constellationapp.network

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Float = 0.7f // 기본값을 지정해두면 유연하게 사용 가능
)

@Serializable
data class Message(
    val role: String,
    val content: String,
)
