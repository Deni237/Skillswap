package com.company.skillswap.viewmodel

import androidx.lifecycle.ViewModel
import com.company.skillswap.model.*
import com.google.firebase.database.*
import kotlinx.coroutines.NonCancellable.children
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationViewModel: ViewModel() {
    private val _requests = MutableStateFlow<List<RequestWithSender>>(emptyList())
    val requests: StateFlow<List<RequestWithSender>> = _requests
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")

    fun loadRequests(userId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("demandes").child(userId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<RequestWithSender>()

                var loadedCount = 0
                snapshot.children.forEach { ds ->
                    val request = ds.getValue(AddRequest::class.java)
                    if (request != null) {
                        usersRef.child(request.senderId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                val user = userSnapshot.getValue(User::class.java)
                                val senderName = "${user?.firstName ?: "Utilisateur"} ${user?.lastName ?: ""}"
                                list.add(RequestWithSender(
                                    senderId = request.senderId,
                                    senderName = senderName,
                                    timestamp = request.timestamp,
                                    read = request.read
                                ))

                                _requests.value = list

                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}