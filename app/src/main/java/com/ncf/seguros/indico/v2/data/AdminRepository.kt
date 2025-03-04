package com.ncf.seguros.indico.v2.data

import com.google.firebase.firestore.FirebaseFirestore
import com.ncf.seguros.indico.v2.model.*
import com.ncf.seguros.indico.v2.util.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AdminRepository @Inject constructor(private val firestore: FirebaseFirestore) {
    fun getDashboardData(): Flow<Result<AdminDashboardData>> = flow {
        try {
            // Implementar lógica de busca de dados do Firestore
            val snapshot = firestore.collection("statistics").document("dashboard").get().await()

            val data = snapshot.toObject(AdminDashboardData::class.java)
            emit(Result.Success(data ?: AdminDashboardData(0, 0, 0, 0)))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    suspend fun updateReferralStatus(referralId: String, newStatus: ReferralStatus): Result<Unit> =
            try {
                firestore
                        .collection("referrals")
                        .document(referralId)
                        .update("status", newStatus)
                        .await()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e)
            }

    fun getPendingReferrals(): Flow<Result<List<ReferralData>>> = flow {
        try {
            val snapshot =
                    firestore
                            .collection("referrals")
                            .whereEqualTo("status", ReferralStatus.PENDING)
                            .get()
                            .await()

            val referrals = snapshot.documents.mapNotNull { it.toObject(ReferralData::class.java) }
            emit(Result.Success(referrals))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}
