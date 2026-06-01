package com.genegebra.healthtracker.domain.repository

import com.genegebra.healthtracker.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, recaptchaToken: String): Result<Unit>
    suspend fun logout()
    suspend fun sendVerificationEmail(): Result<Unit>
    suspend fun reloadUser(): Result<User>
}
