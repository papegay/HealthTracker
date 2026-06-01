package com.genegebra.healthtracker.domain.model

data class User(
    val uid: String,
    val email: String,
    val isAdmin: Boolean = false,
    val isEmailVerified: Boolean = false
)
