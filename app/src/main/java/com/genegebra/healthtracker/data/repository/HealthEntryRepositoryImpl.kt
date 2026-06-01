package com.genegebra.healthtracker.data.repository

import com.genegebra.healthtracker.data.model.HealthEntryDto
import com.genegebra.healthtracker.domain.model.HealthEntry
import com.genegebra.healthtracker.domain.repository.HealthEntryRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthEntryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HealthEntryRepository {

    private val collection get() = firestore.collection("healthEntries")

    override fun getEntriesForUser(userId: String, from: Date?, to: Date?): Flow<List<HealthEntry>> =
        callbackFlow {
            var query = collection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
            if (from != null) query = query.whereGreaterThanOrEqualTo("createdAt", Timestamp(from))
            if (to != null) query = query.whereLessThanOrEqualTo("createdAt", Timestamp(to))

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val entries = snapshot?.documents
                    ?.mapNotNull { it.toObject(HealthEntryDto::class.java)?.toDomain() }
                    ?: emptyList()
                trySend(entries)
            }
            awaitClose { listener.remove() }
        }

    override fun getAllEntries(from: Date?, to: Date?): Flow<List<HealthEntry>> =
        callbackFlow {
            var query = collection.orderBy("createdAt", Query.Direction.DESCENDING)
            if (from != null) query = query.whereGreaterThanOrEqualTo("createdAt", Timestamp(from))
            if (to != null) query = query.whereLessThanOrEqualTo("createdAt", Timestamp(to))

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val entries = snapshot?.documents
                    ?.mapNotNull { it.toObject(HealthEntryDto::class.java)?.toDomain() }
                    ?: emptyList()
                trySend(entries)
            }
            awaitClose { listener.remove() }
        }

    override suspend fun getEntryForSession(userId: String, sessionId: String): HealthEntry? {
        val snapshot = collection
            .whereEqualTo("userId", userId)
            .whereEqualTo("sessionId", sessionId)
            .limit(1)
            .get()
            .await()
        return snapshot.documents.firstOrNull()
            ?.toObject(HealthEntryDto::class.java)
            ?.toDomain()
    }

    override suspend fun saveEntry(entry: HealthEntry): Result<Unit> = runCatching {
        val dto = HealthEntryDto.fromDomain(entry)
        if (entry.id.isEmpty()) {
            collection.add(dto).await()
        } else {
            collection.document(entry.id).set(dto).await()
        }
    }
}
