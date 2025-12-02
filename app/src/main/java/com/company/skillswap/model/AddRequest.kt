package com.company.skillswap.model

data class AddRequest(
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val read: Boolean = false
)
