package com.company.skillswap.model

data class RequestWithSender (
    val Id: String,
    val senderName: String,
    val timestamp: Long,
    val read: Boolean
)
