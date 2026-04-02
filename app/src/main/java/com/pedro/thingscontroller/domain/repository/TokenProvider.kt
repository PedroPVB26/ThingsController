package com.pedro.thingscontroller.domain.repository

interface TokenProvider {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}