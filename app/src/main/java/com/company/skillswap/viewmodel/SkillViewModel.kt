package com.company.skillswap.viewmodel

import androidx.lifecycle.ViewModel
import com.company.skillswap.model.AddRequest
import com.company.skillswap.model.UserSkill
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.company.skillswap.model.User
import com.company.skillswap.model.UserDetail
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth

class SkillViewModel: ViewModel(){
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")

    private val _user = MutableStateFlow<UserDetail?>(null)
    val user: StateFlow<UserDetail?> = _user

    fun loadUserSkill(userId: String) {
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java) ?: return

                // Formatage de la date directement ici
                val birthDateFormatted = if (user.birthDate != 0L) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    sdf.format(Date(user.birthDate))
                } else {
                    "Non défini"
                }

                _user.value = UserDetail(
                    uid = user.uid,
                    username = user.username,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    location = user.location,
                    description = user.description,
                    offeredSkills = user.offeredSkills,
                    desiredSkills = user.desiredSkills,
                    birthDate = birthDateFormatted
                )

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun sendRequest(senderId: String, receiverId: String) {
        val requestsRef = FirebaseDatabase.getInstance()
            .getReference("demandes")
            .child(receiverId)

        val newRequestRef = requestsRef.push()
        val request = AddRequest(
            senderId = senderId,
            receiverId = receiverId,
        )

        newRequestRef.setValue(request)
            .addOnSuccessListener {
                println("Demande envoyée avec succès")
            }
            .addOnFailureListener { e ->
                println("Erreur lors de l'envoi : ${e.message}")
            }
    }

    fun hasSentRequest(senderId: String, receiverId: String, onResult: (Boolean) -> Unit) {
        val requestsRef = FirebaseDatabase.getInstance()
            .getReference("demandes")
            .child(receiverId)

        requestsRef.orderByChild("senderId").equalTo(senderId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onResult(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(false)
                }
            })
    }
}