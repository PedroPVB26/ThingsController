package com.pedro.thingscontroller.data.auth

import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.core.Amplify
import com.pedro.thingscontroller.domain.model.exception.MyAuthException
import com.pedro.thingscontroller.domain.repository.AuthRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    private val TAG = "AuthRepositoryImpl"


    override suspend fun signIn(username: String, password: String) =
        suspendCancellableCoroutine { cont ->
            Amplify.Auth.signIn(
                username,
                password,
                { cont.resume(Unit) },
                { error -> cont.resumeWithException(mapAuthError(error)) }
            )
        }

    override suspend fun signOut(): Unit = suspendCancellableCoroutine { cont ->
        Amplify.Auth.signOut {
            cont.resume(Unit)
        }
    }

    override suspend fun isUserSignedIn(): Boolean =
        suspendCancellableCoroutine { cont ->
            Amplify.Auth.fetchAuthSession(
                { session ->
                    val cognitoSession = session as? com.amplifyframework.auth.cognito.AWSCognitoAuthSession
                    // Se o valor for nulo, o Amplify falhou em obter tokens válidos
                    val hasValidTokens = cognitoSession?.userPoolTokensResult?.value != null
                    cont.resume(session.isSignedIn && hasValidTokens)
                },
                { error ->
                    cont.resume(false)
                }
            )
        }
}

fun mapAuthError(error: AuthException): MyAuthException {

    return when (error) {

        is com.amplifyframework.auth.exceptions.NotAuthorizedException -> {
            MyAuthException.InvalidCredentials()
        }

        else -> {
            MyAuthException.Unknown(error)
        }
    }
}