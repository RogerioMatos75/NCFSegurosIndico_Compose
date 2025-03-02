package com.ncf.seguros.indico.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.ncf.seguros.indico.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Falha na autenticação"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String, phone: String, cpf: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let { user ->
                val newUser = User(
                    id = user.uid,
                    name = name,
                    email = email,
                    phone = phone,
                    cpf = cpf
                )
                usersCollection.document(user.uid).set(newUser).await()
                Result.success(user)
            } ?: Result.failure(Exception("Falha no registro"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getUserData(userId: String): Result<User> {
        return try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java)
            user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Usuário não encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 