package com.genegebra.healthtracker.domain.usecase

import com.genegebra.healthtracker.domain.model.User
import com.genegebra.healthtracker.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        authRepository.login(email, password)
}
