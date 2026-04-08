package com.pedro.thingscontroller.domain.repository

interface AuthRepository {
    suspend fun signIn(username: String, password: String)
    suspend fun signOut()
    suspend fun isUserSignedIn(): Boolean
}