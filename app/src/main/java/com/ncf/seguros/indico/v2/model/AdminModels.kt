package com.ncf.seguros.indico.v2.model

data class AdminDashboardData(
        val totalUsers: Int,
        val pendingReferrals: Int,
        val totalApprovedReferrals: Int,
        val activeUsers: Int
)

data class ReferralData(
        val id: String,
        val clientName: String,
        val phone: String,
        val productType: String,
        val status: ReferralStatus,
        val createdAt: Long,
        val referredBy: String
)

enum class ReferralStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class UserAdminData(
        val uid: String,
        val name: String,
        val email: String,
        val isActive: Boolean,
        val totalReferrals: Int,
        val approvedReferrals: Int
)
