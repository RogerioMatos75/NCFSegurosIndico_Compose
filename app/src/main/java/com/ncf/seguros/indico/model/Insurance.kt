package com.ncf.seguros.indico.model

import java.util.Date

data class Insurance(
    val id: String = "",
    val condominiumId: String = "",
    val policyNumber: String = "",
    val insuranceCompany: String = "",
    val coverageType: String = "",
    val coverageAmount: Double = 0.0,
    val startDate: Long = 0,
    val endDate: Long = 0,
    val premium: Double = 0.0,
    val status: String = "active",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isActive(): Boolean {
        val currentTime = System.currentTimeMillis()
        return status == "active" && currentTime in startDate..endDate
    }
    
    fun getRemainingDays(): Int {
        val currentTime = System.currentTimeMillis()
        if (currentTime > endDate) return 0
        return ((endDate - currentTime) / (1000 * 60 * 60 * 24)).toInt()
    }
} 