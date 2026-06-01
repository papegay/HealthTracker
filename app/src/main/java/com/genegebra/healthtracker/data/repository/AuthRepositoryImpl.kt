package com.genegebra.healthtracker.data.repository

import com.genegebra.healthtracker.data.model.UserDto
import com.genegebra.healthtracker.domain.model.User
import com.genegebra.healthtracker.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val fbUser = firebaseAuth.currentUser
            if (fbUser == null) {
                trySend(null)
            } else {
                firestore.collection("users").document(fbUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val dto = doc.toObject(UserDto::class.java)
                        trySend(dto?.toDomain(fbUser.isEmailVerified))
                    }
                    .addOnFailureListener { trySend(null) }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val fbUser = result.user ?: error("Login failed")
        val doc = firestore.collection("users").document(fbUser.uid).get().await()
        val dto = doc.toObject(UserDto::class.java) ?: UserDto(fbUser.uid, fbUser.email ?: "")
        dto.toDomain(fbUser.isEmailVerified)
    }

    override suspend fun register(
        email: String,
        password: String,
        recaptchaToken: String
    ): Result<Unit> = runCatching {
        // recaptchaToken should be verified server-side if a backend is added.
        // Here we proceed with Firebase Auth registration and rely on email verification.
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val fbUser = result.user ?: error("Registration failed")
        val userDto = UserDto(uid = fbUser.uid, email = email, isAdmin = false)
        firestore.collection("users").document(fbUser.uid).set(userDto).await()
        fbUser.sendEmailVerification().await()
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun sendVerificationEmail(): Result<Unit> = runCatching {
        auth.currentUser?.sendEmailVerification()?.await() ?: error("No user logged in")
    }

    override suspend fun reloadUser(): Result<User> = runCatching {
        val fbUser = auth.currentUser ?: error("No user logged in")
        fbUser.reload().await()
        val doc = firestore.collection("users").document(fbUser.uid).get().await()
        val dto = doc.toObject(UserDto::class.java) ?: UserDto(fbUser.uid, fbUser.email ?: "")
        dto.toDomain(fbUser.isEmailVerified)
    }
}
