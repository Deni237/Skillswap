package com.company.skillswap.viewmodel

import androidx.lifecycle.ViewModel
import com.company.skillswap.model.User
import com.company.skillswap.model.UserDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val usersRef = FirebaseDatabase.getInstance().getReference("users")

    private val _currentUser = MutableStateFlow<UserDetail?>(null)
    val currentUser: StateFlow<UserDetail?> = _currentUser

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        val uid = auth.currentUser?.uid ?: return  // Sécurise si pas connecté

        usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java) ?: return

                // Formatage de la date
                val birthDateFormatted = if (user.birthDate != 0L) {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    sdf.format(Date(user.birthDate))
                } else "Non défini"

                _currentUser.value = UserDetail(
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

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}