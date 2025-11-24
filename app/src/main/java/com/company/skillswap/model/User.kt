package com.company.skillswap.model

import android.R

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val profileCompleted: Boolean = false
)