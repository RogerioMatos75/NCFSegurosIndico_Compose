package com.ncf.seguros.indico.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ncf.seguros.indico.model.Indication
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IndicationRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val indicationsCollection = firestore.collection("indications")
    
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    fun getIndicationsForCurrentUser(): Flow<List<Indication>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuário não autenticado")
        
        val listener = indicationsCollection
            .whereEqualTo("referrerUserId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val indications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Indication::class.java)
                } ?: emptyList()
                
                trySend(indications)
            }
        
        awaitClose { listener.remove() }
    }
    
    fun getAllIndications(): Flow<List<Indication>> = callbackFlow {
        val listener = indicationsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val indications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Indication::class.java)
                } ?: emptyList()
                
                trySend(indications)
            }
        
        awaitClose { listener.remove() }
    }
    
    suspend fun addIndication(indication: Indication): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuário não autenticado")
            val userName = auth.currentUser?.displayName ?: ""
            
            val newIndication = indication.copy(
                referrerUserId = userId,
                referrerName = userName,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            val docRef = indicationsCollection.add(newIndication).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateIndicationStatus(indicationId: String, status: String, notes: String = ""): Result<Unit> {
        return try {
            indicationsCollection.document(indicationId)
                .update(
                    mapOf(
                        "status" to status,
                        "notes" to notes,
                        "updatedAt" to System.currentTimeMillis()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun applyDiscount(indicationId: String): Result<Unit> {
        return try {
            indicationsCollection.document(indicationId)
                .update("discountApplied", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getIndicationById(indicationId: String): Result<Indication> {
        return try {
            val doc = indicationsCollection.document(indicationId).get().await()
            val indication = doc.toObject(Indication::class.java)
                ?: throw IllegalStateException("Indicação não encontrada")
            Result.success(indication)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteIndication(indicationId: String): Result<Unit> {
        return try {
            indicationsCollection.document(indicationId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserTotalDiscount(): Result<Int> {
        return try {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("Usuário não autenticado")
            
            val convertedIndications = indicationsCollection
                .whereEqualTo("referrerUserId", userId)
                .whereEqualTo("status", "converted")
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Indication::class.java) }
            
            // 1% por indicação + 1% adicional se convertida, máximo de 10%
            val discount = minOf(convertedIndications.size * 2, 10)
            Result.success(discount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 