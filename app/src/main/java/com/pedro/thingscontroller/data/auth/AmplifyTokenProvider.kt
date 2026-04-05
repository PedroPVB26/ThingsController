package com.pedro.thingscontroller.data.auth

import android.content.Context
import android.util.Log
import com.amplifyframework.auth.AuthSession
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.core.Amplify
import com.pedro.thingscontroller.domain.model.Tokens
import com.pedro.thingscontroller.domain.repository.TokenProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AmplifyTokenProvider @Inject constructor(
    @ApplicationContext private val context: Context
): TokenProvider{
    override suspend fun getTokens(): Tokens? {
        try{
            val session = fetchSession() as AWSCognitoAuthSession
            val accessToken = session.userPoolTokensResult.value?.accessToken
            val refreshToken = session.userPoolTokensResult.value?.refreshToken
            val idToken = session.userPoolTokensResult.value?.idToken
            return Tokens(idToken, accessToken, refreshToken)
        } catch (e: Exception) {
            return null
        }
    }

    private suspend fun fetchSession(): AuthSession =
        suspendCancellableCoroutine { cont ->
            Amplify.Auth.fetchAuthSession(
                { session ->
                    cont.resume(session)
                },
                { error ->
                    cont.resumeWithException(error)
                }
            )
        }

    override suspend fun refreshToken(): String? = getTokens()?.refreshToken
}