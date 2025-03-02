package com.ncf.seguros.indico.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ncf.seguros.indico.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun updateUserProfile(name: String, phone: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
            val updates = hashMapOf<String, Any>(
                "name" to name,
                "phone" to phone
            )
            usersCollection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateFCMToken(token: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.success(Unit) // Não fazer nada se não estiver logado
            val updates = hashMapOf<String, Any>(
                "fcmToken" to token
            )
            usersCollection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(imageUrl: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
            val updates = hashMapOf<String, Any>(
                "profileImageUrl" to imageUrl
            )
            usersCollection.document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 