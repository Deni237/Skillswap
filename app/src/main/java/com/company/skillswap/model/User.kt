package com.company.skillswap.model

import android.R

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val offeredSkills: List<String> = emptyList(),
    val desiredSkills: List<String> = emptyList(),
    val favoriteSkills: List<String> = emptyList(),
    val birthDate: Long = 0L,
    val location: String = "",
    val description: String = "",
    val profileCompleted: Boolean = false
)