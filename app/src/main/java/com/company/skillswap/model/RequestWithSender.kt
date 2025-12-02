package com.company.skillswap.model

data class RequestWithSender (
    val senderId: String,
    val senderName: String,
    val timestamp: Long,
    val read: Boolean
)
