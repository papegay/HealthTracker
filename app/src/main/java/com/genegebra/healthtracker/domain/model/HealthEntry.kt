package com.genegebra.healthtracker.domain.model

import java.util.Date

data class HealthEntry(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val sessionId: String = "",
    val systolic: Int? = null,
    val diastolic: Int? = null,
    val pulse: Int? = null,
    val anxietyLevel: Int? = null,
    val createdAt: Date = Date()
) {
    val hasAnyValue: Boolean
        get() = systolic != null || diastolic != null || pulse != null || anxietyLevel != null
}
