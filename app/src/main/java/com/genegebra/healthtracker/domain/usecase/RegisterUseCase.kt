package com.genegebra.healthtracker.domain.usecase

import com.genegebra.healthtracker.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        recaptchaToken: String
    ): Result<Unit> = authRepository.register(email, password, recaptchaToken)
}
