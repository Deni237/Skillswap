package com.company.skillswap.viewmodel

import androidx.lifecycle.ViewModel
import com.company.skillswap.model.*
import com.google.firebase.database.*
import kotlinx.coroutines.NonCancellable.children
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class NotificationWithSender(
    val Id: String,
    val senderName: String,
    val type: String,
    val timestamp: Long,
    val requestId:String,
    val receiverId: String = "",
    val senderId: String = "",
    val read: Boolean
)
class NotificationViewModel: ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationWithSender>>(emptyList())
    val notifications: StateFlow<List<NotificationWithSender>> = _notifications

    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val notificationsRef = FirebaseDatabase.getInstance().getReference("notifications")

    fun loadNotifications(userId: String) {
        notificationsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<NotificationWithSender>()
                var remaining = 0

                snapshot.children.forEach { ds ->
                    val notif = ds.getValue(Notification::class.java)

                    if (notif != null && notif.receiverId == userId) {
                        remaining++

                        usersRef.child(notif.senderId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {

                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    val user = userSnapshot.getValue(User::class.java)
                                    val senderName =
                                        "${user?.firstName ?: "Utilisateur"} ${user?.lastName ?: ""}"

                                    list.add(
                                        NotificationWithSender(
                                            Id = notif.notificationId,
                                            senderName = senderName,
                                            type = notif.type,
                                            timestamp = notif.timestamp,
                                            requestId = notif.requestId,
                                            senderId = notif.senderId,
                                            receiverId = notif.receiverId,
                                            read = notif.read
                                        )
                                    )

                                    remaining--
                                    if (remaining == 0) {
                                        _notifications.value =
                                            list.sortedByDescending { it.timestamp }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    remaining--
                                    if (remaining == 0) {
                                        _notifications.value = list
                                    }
                                }
                            })
                    }
                }

                if (remaining == 0) {
                    _notifications.value = emptyList()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    fun markAsRead(notificationId: String) {
        notificationsRef.child(notificationId).child("read").setValue(true)
    }
}