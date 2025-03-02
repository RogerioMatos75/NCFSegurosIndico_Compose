package com.ncf.seguros.indico.model

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Indication(
    @DocumentId
    val id: String = "",
    val referrerUserId: String = "", // ID do usuário que fez a indicação
    val referrerName: String = "", // Nome do usuário que fez a indicação
    val name: String = "", // Nome da pessoa indicada
    val phone: String = "", // Telefone da pessoa indicada
    val email: String = "", // Email da pessoa indicada
    val vehicleType: String = "", // Tipo de veículo
    val vehicleModel: String = "", // Modelo do veículo
    val vehicleYear: String = "", // Ano do veículo
    val status: String = "pending", // pending, contacted, converted, rejected
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val notes: String = "", // Notas adicionais sobre a indicação
    val discountApplied: Boolean = false // Se o desconto já foi aplicado ao referrer
) {
    fun getStatusDisplay(): String {
        return when (status) {
            "pending" -> "Pendente"
            "contacted" -> "Contatado"
            "converted" -> "Convertido"
            "rejected" -> "Rejeitado"
            else -> "Desconhecido"
        }
    }
    
    fun getFormattedDate(): String {
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        return dateFormat.format(Date(createdAt))
    }
} 