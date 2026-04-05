package com.pedro.thingscontroller.domain.repository

interface AuthRepository {
    suspend fun signIn(username: String, password: String): Boolean
    suspend fun signOut()
    suspend fun isUserSignedIn(): Boolean
}