package com.pedro.thingscontroller.domain.usecase

import com.pedro.thingscontroller.domain.model.Tokens
import com.pedro.thingscontroller.domain.model.UseCaseResult
import com.pedro.thingscontroller.domain.model.exception.MyAuthException
import com.pedro.thingscontroller.domain.repository.AuthRepository
import com.pedro.thingscontroller.domain.repository.TokenProvider
import javax.inject.Inject

class CheckAuthStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenProvider: TokenProvider,
    private val getNetworkStatusUseCase: GetNetworkStatusUseCase
) {
    suspend operator fun invoke(): UseCaseResult<Tokens?> {
        getNetworkStatusUseCase().let {
            if (it is UseCaseResult.Failure.NoNetwork) return it
        }

        return try {
            val alreadyLoggedIn = authRepository.isUserSignedIn()

            if (alreadyLoggedIn) {
                val tokens = tokenProvider.getTokens()
                // Se chegou aqui, o isUserSignedIn já garantiu que o token é válido/renovado
                UseCaseResult.Success(tokens)
            } else {
                UseCaseResult.Failure.AuthError(MyAuthException.SessionExperied())
            }
        } catch (e: Exception) {
            UseCaseResult.Failure.Unknown(e)
        }
    }
}