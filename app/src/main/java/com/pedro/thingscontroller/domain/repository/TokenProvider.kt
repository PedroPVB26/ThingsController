package com.pedro.thingscontroller.domain.repository

import com.pedro.thingscontroller.domain.model.Tokens

interface TokenProvider {
    suspend fun getTokens(): Tokens?
//    suspend fun saveToken(accessToken: String, refreshToken: String)
    suspend fun refreshToken(): String?
//    suspend fun clearTokens()
}