package com.genegebra.healthtracker.domain.model

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    var sessionId: String = UUID.randomUUID().toString()
        private set

    fun newSession() {
        sessionId = UUID.randomUUID().toString()
    }
}
