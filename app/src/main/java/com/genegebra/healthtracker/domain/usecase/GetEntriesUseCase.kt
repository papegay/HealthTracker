package com.genegebra.healthtracker.domain.usecase

import com.genegebra.healthtracker.domain.model.HealthEntry
import com.genegebra.healthtracker.domain.repository.HealthEntryRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class GetEntriesUseCase @Inject constructor(private val repository: HealthEntryRepository) {
    operator fun invoke(
        userId: String,
        isAdmin: Boolean,
        from: Date? = null,
        to: Date? = null
    ): Flow<List<HealthEntry>> =
        if (isAdmin) repository.getAllEntries(from, to)
        else repository.getEntriesForUser(userId, from, to)
}
