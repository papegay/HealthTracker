package com.genegebra.healthtracker.domain.usecase

import com.genegebra.healthtracker.domain.model.HealthEntry
import com.genegebra.healthtracker.domain.repository.HealthEntryRepository
import javax.inject.Inject

class SaveEntryUseCase @Inject constructor(private val repository: HealthEntryRepository) {
    suspend operator fun invoke(entry: HealthEntry): Result<Unit> = repository.saveEntry(entry)
}
