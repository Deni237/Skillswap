package com.company.skillswap.viewmodel

import androidx.lifecycle.ViewModel
import com.company.skillswap.model.ConversationItem
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MessagesViewModel: ViewModel() {

    private val _conversations = MutableStateFlow<List<ConversationItem>>(emptyList())
    val conversations: StateFlow<List<ConversationItem>> = _conversations

    private val db = FirebaseDatabase.getInstance()
    private val usersRef = db.getReference("users")
    private val chatsRef = db.getReference("user_chats")

    fun loadConversations(currentUserId: String) {
        chatsRef.child(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (!snapshot.exists()) {
                        _conversations.value = emptyList()
                        return
                    }

                    val tempList = mutableListOf<ConversationItem>()
                    val total = snapshot.childrenCount.toInt()
                    var loaded = 0

                    snapshot.children.forEach { child ->
                        val userId = child.key ?: return@forEach
                        val lastMessage = child.child("lastMessage")
                            .getValue(String::class.java) ?: ""
                        val timestamp = child.child("timestamp")
                            .getValue(Long::class.java) ?: 0L

                        // 🔹 Charger le nom depuis /users
                        usersRef.child(userId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnap: DataSnapshot) {
                                    val firstName = userSnap.child("firstName")
                                        .getValue(String::class.java) ?: ""
                                    val lastName = userSnap.child("lastName")
                                        .getValue(String::class.java) ?: ""

                                    tempList.add(
                                        ConversationItem(
                                            userId = userId,
                                            name = "$firstName $lastName".trim(),
                                            lastMessage = lastMessage,
                                            timestamp = timestamp
                                        )
                                    )

                                    loaded++
                                    if (loaded == total) {
                                        _conversations.value =
                                            tempList.sortedByDescending { it.timestamp }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    loaded++
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}