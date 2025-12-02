package com.company.skillswap.model

data class UserSkill(
    val firstName: String = "",
    val lastName: String = "",
    val city: String = "",
    val competences: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val userId: String = ""
)
