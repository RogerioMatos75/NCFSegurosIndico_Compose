package com.ncf.seguros.indico.model

data class Condominium(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val address: String = "",
    val number: String = "",
    val complement: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val createdAt: Long = System.currentTimeMillis()
) 