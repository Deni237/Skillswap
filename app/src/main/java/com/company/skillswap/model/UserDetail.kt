package com.company.skillswap.model

data class UserDetail(
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val location: String,
    val description: String,
    val offeredSkills: List<String>,
    val desiredSkills: List<String>,
    val birthDate: String
)
