package com.ncf.seguros.indico.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ncf.seguros.indico.model.Condominium
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CondominiumRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val condominiumsCollection = firestore.collection("condominiums")

    suspend fun getCondominiumsForUser(): Flow<List<Condominium>> = flow {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
            val snapshot = condominiumsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val condominiums = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Condominium::class.java)?.copy(id = doc.id)
            }
            
            emit(condominiums)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun getCondominiumById(condominiumId: String): Result<Condominium> {
        return try {
            val document = condominiumsCollection.document(condominiumId).get().await()
            val condominium = document.toObject(Condominium::class.java)?.copy(id = document.id)
            condominium?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Condomínio não encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCondominium(condominium: Condominium): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
            val newCondominium = condominium.copy(userId = userId)
            val docRef = condominiumsCollection.add(newCondominium).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCondominium(condominium: Condominium): Result<Unit> {
        return try {
            condominiumsCollection.document(condominium.id).set(condominium).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCondominium(condominiumId: String): Result<Unit> {
        return try {
            condominiumsCollection.document(condominiumId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 