package com.ncf.seguros.indico.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val cpf: String = "",
    val profileImageUrl: String = "",
    val fcmToken: String = "",
    val createdAt: Long = System.currentTimeMillis()
) 