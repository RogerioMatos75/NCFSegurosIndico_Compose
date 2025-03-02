package com.ncf.seguros.indico.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ncf.seguros.indico.model.Insurance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsuranceRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val insurancesCollection = firestore.collection("insurances")

    suspend fun getInsurancesForCondominium(condominiumId: String): Flow<List<Insurance>> = flow {
        try {
            val snapshot = insurancesCollection
                .whereEqualTo("condominiumId", condominiumId)
                .get()
                .await()
            
            val insurances = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Insurance::class.java)?.copy(id = doc.id)
            }
            
            emit(insurances)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun addInsurance(insurance: Insurance): Result<String> {
        return try {
            val docRef = insurancesCollection.add(insurance).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateInsurance(insurance: Insurance): Result<Unit> {
        return try {
            insurancesCollection.document(insurance.id).set(insurance).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteInsurance(insuranceId: String): Result<Unit> {
        return try {
            insurancesCollection.document(insuranceId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 