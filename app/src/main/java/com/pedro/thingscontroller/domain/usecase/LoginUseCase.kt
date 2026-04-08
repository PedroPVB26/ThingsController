package com.pedro.thingscontroller.domain.usecase


import com.pedro.thingscontroller.domain.model.Tokens
import com.pedro.thingscontroller.domain.model.UseCaseResult
import com.pedro.thingscontroller.domain.model.exception.MyAuthException
import com.pedro.thingscontroller.domain.repository.AuthRepository
import com.pedro.thingscontroller.domain.repository.TokenProvider
import javax.inject.Inject


class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenProvider: TokenProvider,
    private val ensureNetworkUseCase: EnsureNetworkUseCase
) {
    suspend operator fun invoke( email: String, password: String): UseCaseResult<Tokens?> {
        ensureNetworkUseCase().let {
            if(it is UseCaseResult.Failure.NoNetwork) return it
        }

        return try {
            val alreadyLoggedIn = authRepository.isUserSignedIn()

            if (alreadyLoggedIn) {
                authRepository.signOut()
//                val tokens = tokenProvider.getTokens()
//                return UseCaseResult.Success(tokens)
            }

            authRepository.signIn(email, password)

            val tokens = tokenProvider.getTokens()
            UseCaseResult.Success(tokens)

        } catch (e: Exception) {
            when(e){
                is MyAuthException -> {
                    UseCaseResult.Failure.AuthError(e)
                }

                else -> {
                    UseCaseResult.Failure.Unknown(e)
                }
            }
        }
    }
}