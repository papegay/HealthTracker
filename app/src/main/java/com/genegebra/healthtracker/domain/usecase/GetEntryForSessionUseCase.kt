package com.genegebra.healthtracker.domain.usecase

import com.genegebra.healthtracker.domain.model.HealthEntry
import com.genegebra.healthtracker.domain.repository.HealthEntryRepository
import javax.inject.Inject

class GetEntryForSessionUseCase @Inject constructor(private val repository: HealthEntryRepository) {
    suspend operator fun invoke(userId: String, sessionId: String): HealthEntry? =
        repository.getEntryForSession(userId, sessionId)
}
