package com.company.skillswap.viewmodel

import androidx.lifecycle.ViewModel
import com.company.skillswap.model.ChatMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.database.ValueEventListener
class ChatViewModel: ViewModel() {
    private val db = FirebaseDatabase.getInstance().getReference("messages")
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val _receiverName = MutableStateFlow("Conversation")
    val receiverName: StateFlow<String> = _receiverName
    fun getMessages(senderId: String, receiverId: String): StateFlow<List<ChatMessage>> = _messages

    fun loadMessages(senderId: String, receiverId: String) {
        val chatId = getChatId(senderId, receiverId)
        db.child(chatId).orderByChild("timestamp")
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val list = mutableListOf<ChatMessage>()
                    snapshot.children.forEach { ds ->
                        val msg = ds.getValue(ChatMessage::class.java)
                        if (msg != null) list.add(msg)
                    }
                    _messages.value = list
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
            })
    }

    fun sendMessage(senderId: String, receiverId: String, text: String) {
        val chatId = getChatId(senderId, receiverId)
        val timestamp = System.currentTimeMillis()

        val msgRef = db.child(chatId).push()
        val msg = ChatMessage(
            messageId = msgRef.key ?: "",
            senderId = senderId,
            receiverId = receiverId,
            text = text,
            timestamp = timestamp
        )

        msgRef.setValue(msg)

        val updates = mapOf(
            "lastMessage" to text,
            "timestamp" to timestamp
        )

        val root = FirebaseDatabase.getInstance().reference
        root.child("user_chats")
            .child(senderId)
            .child(receiverId)
            .updateChildren(updates)

        root.child("user_chats")
            .child(receiverId)
            .child(senderId)
            .updateChildren(updates)
    }

    fun loadReceiverName(receiverId: String) {
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(receiverId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firstName = snapshot.child("firstName").getValue(String::class.java) ?: ""
                    val lastName = snapshot.child("lastName").getValue(String::class.java) ?: ""
                    _receiverName.value = "$firstName $lastName".trim()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun getChatId(user1: String, user2: String): String {
        return if (user1 < user2) "$user1-$user2" else "$user2-$user1"
    }
}