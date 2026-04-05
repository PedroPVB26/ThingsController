package com.pedro.thingscontroller.domain.usecase


import com.pedro.thingscontroller.domain.model.Tokens
import com.pedro.thingscontroller.domain.model.UseCaseResult
import com.pedro.thingscontroller.domain.repository.AuthRepository
import com.pedro.thingscontroller.domain.repository.TokenProvider
import javax.inject.Inject


class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenProvider: TokenProvider,
    private val ensureNetworkUseCase: EnsureNetworkUseCase
) {
    suspend operator fun invoke(
        username: String,
        password: String
    ): UseCaseResult<Tokens?> {
        ensureNetworkUseCase().let {
            if(it is UseCaseResult.Failure.NoNetwork) return it
        }

        return try {
            val alreadyLoggedIn = authRepository.isUserSignedIn()

            if (alreadyLoggedIn) {
                val tokens = tokenProvider.getTokens()
                return UseCaseResult.Success(tokens)
            }

            val success = authRepository.signIn(username, password)

            if (success) {
                val tokens = tokenProvider.getTokens()
                UseCaseResult.Success(tokens)
            } else {
                UseCaseResult.Failure.Unknown(Exception("Login não concluído"))
            }

        } catch (e: Exception) {
            UseCaseResult.Failure.Unknown(e)
        }
    }
}