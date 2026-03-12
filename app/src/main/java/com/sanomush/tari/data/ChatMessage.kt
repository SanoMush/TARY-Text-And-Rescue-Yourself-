package com.sanomush.tari.data

data class ChatMessage(
    val message: String,
    val isUser: Boolean // true kalau yang ngirim user, false kalau TARY
)