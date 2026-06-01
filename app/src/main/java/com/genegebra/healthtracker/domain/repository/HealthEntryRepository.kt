package com.genegebra.healthtracker.domain.repository

import com.genegebra.healthtracker.domain.model.HealthEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface HealthEntryRepository {
    fun getEntriesForUser(userId: String, from: Date? = null, to: Date? = null): Flow<List<HealthEntry>>
    fun getAllEntries(from: Date? = null, to: Date? = null): Flow<List<HealthEntry>>
    suspend fun getEntryForSession(userId: String, sessionId: String): HealthEntry?
    suspend fun saveEntry(entry: HealthEntry): Result<Unit>
}
