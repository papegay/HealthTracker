package com.genegebra.healthtracker.data.model

import com.genegebra.healthtracker.domain.model.User
import com.google.firebase.firestore.DocumentId

data class UserDto(
    @DocumentId val uid: String = "",
    val email: String = "",
    val isAdmin: Boolean = false
) {
    fun toDomain(isEmailVerified: Boolean) = User(
        uid = uid,
        email = email,
        isAdmin = isAdmin,
        isEmailVerified = isEmailVerified
    )
}
