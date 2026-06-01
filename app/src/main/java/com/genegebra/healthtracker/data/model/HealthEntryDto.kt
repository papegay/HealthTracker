package com.genegebra.healthtracker.data.model

import com.genegebra.healthtracker.domain.model.HealthEntry
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class HealthEntryDto(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val sessionId: String = "",
    val systolic: Int? = null,
    val diastolic: Int? = null,
    val pulse: Int? = null,
    val anxietyLevel: Int? = null,
    val createdAt: Timestamp = Timestamp.now()
) {
    fun toDomain() = HealthEntry(
        id = id,
        userId = userId,
        userEmail = userEmail,
        sessionId = sessionId,
        systolic = systolic,
        diastolic = diastolic,
        pulse = pulse,
        anxietyLevel = anxietyLevel,
        createdAt = createdAt.toDate()
    )

    companion object {
        fun fromDomain(entry: HealthEntry) = HealthEntryDto(
            id = entry.id,
            userId = entry.userId,
            userEmail = entry.userEmail,
            sessionId = entry.sessionId,
            systolic = entry.systolic,
            diastolic = entry.diastolic,
            pulse = entry.pulse,
            anxietyLevel = entry.anxietyLevel,
            createdAt = Timestamp(entry.createdAt)
        )
    }
}
