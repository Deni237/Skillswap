package com.company.skillswap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.skillswap.model.AddRequest
import com.company.skillswap.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RequestViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance().getReference("demandes")
    private val auth = FirebaseAuth.getInstance()

    private val _requests = MutableStateFlow<List<AddRequest>>(emptyList())
    val requests: StateFlow<List<AddRequest>> = _requests

    private val _selectedRequest = MutableStateFlow<AddRequest?>(null)
    val selectedRequest: StateFlow<AddRequest?> = _selectedRequest.asStateFlow()

    /**
     * Charger toutes les demandes reçues par l'utilisateur connecté
     */
    fun loadRequests(currentUserId: String) {
        db.child(currentUserId).get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<AddRequest>()
            for (child in snapshot.children) {
                val request = child.getValue(AddRequest::class.java)
                if (request != null) list.add(request)
            }
            _requests.value = list
        }
    }

    /** Récupérer une demande spécifique par requestId */
    fun getRequest(requestId: String): StateFlow<AddRequest?> {
        viewModelScope.launch {
            db.child(requestId).get().addOnSuccessListener { snapshot ->
                val request = snapshot.getValue(AddRequest::class.java)
                request?.requestId = snapshot.key ?: ""
                _selectedRequest.value = request
            }
        }
        return selectedRequest
    }


    /**
     * Marquer une demande comme lue
     */

    fun markAsRead(requestId: String) {
        db.child(requestId).child("read").setValue(true)

        _requests.value = _requests.value.map {
            if (it.requestId == requestId) it.copy(read = true) else it
        }

        _selectedRequest.value = _selectedRequest.value?.takeIf { it.requestId == requestId }?.copy(read = true)
    }



    private fun createAcceptedNotification(
        senderId: String,
        receiverId: String,
        requestId: String
    ) {
        val notificationsRef =
            FirebaseDatabase.getInstance().getReference("notifications")

        val newNotifRef = notificationsRef.push()
        val notificationId = newNotifRef.key ?: return

        val notification = Notification(
            notificationId = notificationId,
            senderId = senderId,      // celui qui accepte
            receiverId = receiverId,  // celui qui reçoit la notif
            requestId = requestId,
            type = "request_accepted",
            timestamp = System.currentTimeMillis(),
            read = false
        )

        newNotifRef.setValue(notification)
    }
    /**
     * Accepter ou refuser une demande
     */
    fun respondToRequest(requestId: String, accepted: Boolean) {
        val updates = mapOf(
            "status" to if (accepted) "Accepter" else "Refuser",
            "read" to true,
            "timestamp" to System.currentTimeMillis()
        )

        db.child(requestId).updateChildren(updates)
            .addOnSuccessListener {

                // Mise à jour locale
                _requests.value = _requests.value.map {
                    if (it.requestId == requestId) {
                        it.copy(
                            status = updates["status"] as String,
                            read = true
                        )
                    } else it
                }

                _selectedRequest.value = _selectedRequest.value?.takeIf { it.requestId == requestId }?.copy(
                    status = updates["status"] as String,
                    read = true
                )

//                _selectedRequest.value?.let {
//                    if (it.requestId == requestId) {
//                        _selectedRequest.value = it.copy(
//                            status = updates["status"] as String,
//                            read = true
//                        )
//                    }
//                }

                // Récupérer la demande
                val request =
                    _requests.value.find { it.requestId == requestId }
                        ?: _selectedRequest.value
                        ?: return@addOnSuccessListener

                // 🔔 CRÉER NOTIFICATION SI ACCEPTÉE
                if (accepted) {
                    createAcceptedNotification(
                        senderId = auth.currentUser?.uid ?: "",
                        receiverId = request.senderId,
                        requestId = requestId
                    )
                }
            }
    }


    /**
     * Vérifier si une demande a déjà été envoyée
     */
    fun hasSentRequest(senderId: String, receiverId: String, callback: (Boolean) -> Unit) {
        db.child(receiverId).child(senderId).get().addOnSuccessListener { snapshot ->
            callback(snapshot.exists())
        }
    }

}
