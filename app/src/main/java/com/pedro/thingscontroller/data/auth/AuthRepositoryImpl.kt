package com.pedro.thingscontroller.data.auth

import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.core.Amplify
import com.pedro.thingscontroller.domain.repository.AuthRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    override suspend fun signIn(username: String, password: String): Boolean =
        suspendCancellableCoroutine { cont ->
            Amplify.Auth.signIn(
                username,
                password,
                { result -> cont.resume(result.isSignedIn) },
                { error -> cont.resumeWithException(error) }
            )
        }

    override suspend fun signOut() {
        Amplify.Auth.signOut({} as AuthSignOutOptions, {})
    }

    override suspend fun isUserSignedIn(): Boolean =
        suspendCancellableCoroutine { cont ->
            Amplify.Auth.fetchAuthSession(
                { session ->
                    cont.resume(session.isSignedIn)
                },
                { error ->
                    cont.resumeWithException(error)
                }
            )
        }
}