package com.pedro.thingscontroller.domain.model

data class Tokens(
    val idToken: String?,
    val accessToken: String?,
    val refreshToken: String?
)