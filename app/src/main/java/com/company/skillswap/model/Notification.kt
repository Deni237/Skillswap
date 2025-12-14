package com.company.skillswap.model

data class Notification(
    val notificationId: String = "",
    val receiverId: String = "",
    val senderId: String = "",
    val requestId: String = "",
    val type: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val read: Boolean = false
)
