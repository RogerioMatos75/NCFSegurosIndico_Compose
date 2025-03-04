package com.ncf.seguros.indico.v2.util

object Validators {
    private val EMAIL_REGEX = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult.Error("Email não pode estar vazio")
            !email.matches(EMAIL_REGEX) -> ValidationResult.Error("Email inválido")
            else -> ValidationResult.Success
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult.Error("Senha não pode estar vazia")
            password.length < 6 -> ValidationResult.Error("Senha deve ter pelo menos 6 caracteres")
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
