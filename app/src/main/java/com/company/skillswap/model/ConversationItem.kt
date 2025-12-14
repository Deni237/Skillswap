package com.company.skillswap.model

data class ConversationItem(
    val userId: String = "",
    val name: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L
)
