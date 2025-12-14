package com.company.skillswap.model

data class AddRequest(
    var requestId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = 0L,
    val read: Boolean = false,
    val status: String = ""
)
