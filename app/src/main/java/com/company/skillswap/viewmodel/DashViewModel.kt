package com.company.skillswap.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.company.skillswap.model.UserSkill
import com.google.firebase.database.DataSnapshot
import com.company.skillswap.model.User



class DashViewModel : ViewModel() {

    private val usersRef = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()

    // Liste complète chargée une seule fois
    private val _allSkills = mutableListOf<UserSkill>()

    private val _skills = MutableStateFlow<List<UserSkill>>(emptyList())
    val skills: StateFlow<List<UserSkill>> = _skills

    private val _userLocation = MutableStateFlow<String?>(null)
    val userLocation: StateFlow<String?> = _userLocation

    init {
        loadUserLocation()
    }

    private fun loadUserLocation() {
        val uid = auth.currentUser?.uid ?: return

        usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _userLocation.value = snapshot.child("location").value as? String
                loadSkills()
            }

            override fun onCancelled(error: DatabaseError) {
                loadSkills()
            }
        })
    }

    fun loadSkills() {
        val currentUserUid = auth.currentUser?.uid

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _allSkills.clear()

                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java) ?: continue
                    if (user.uid == currentUserUid) continue

                    val userLocation = user.location ?: ""

                    user.offeredSkills.forEach { skill ->
                        _allSkills.add(
                            UserSkill(
                                firstName = user.firstName,
                                lastName = user.lastName,
                                city = userLocation,
                                competence = skill,
                                userId = user.uid
                            )
                        )
                    }
                }

                _skills.value = _allSkills
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Filtrage en local (plus rapide, plus propre)
    fun filterSkills(skillQuery: String, cityQuery: String) {
        val filtered = _allSkills.filter { skill ->
            val matchesSkill = skillQuery.isBlank() ||
                    skill.competence.contains(skillQuery, ignoreCase = true)

            val matchesCity = cityQuery.isBlank() ||
                    skill.city.contains(cityQuery, ignoreCase = true)

            matchesSkill && matchesCity
        }

        _skills.value = filtered
    }

}
