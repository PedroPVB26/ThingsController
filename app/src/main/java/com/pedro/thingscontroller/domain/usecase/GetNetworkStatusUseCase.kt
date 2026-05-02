package com.pedro.thingscontroller.domain.usecase

import com.pedro.thingscontroller.domain.repository.NetworkMonitor
import com.pedro.thingscontroller.domain.model.UseCaseResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case responsible for verifying whether the android device currently
 * has an active internet connection.
 *
 * This is a utility use case meant to be composed into other use cases
 * that require network access before performing any operation, keeping
 * connectivity concerns centralized in a single place.
 *
 * @see [UseCaseResult.Failure.NoNetwork]
 */
class GetNetworkStatusUseCase @Inject constructor(
    private val networkMonitor: NetworkMonitor
) {
    /**
     * Performs a one-shot check of the current network state.
     *
     * @return [UseCaseResult.Success] if the device is online,
     * or [UseCaseResult.Failure.NoNetwork] if there is no active connection.
     */
    suspend operator fun invoke(): UseCaseResult<Unit> {
        val isOnline = networkMonitor.isOnline.first()
        return if (isOnline) UseCaseResult.Success(Unit)
        else UseCaseResult.Failure.NoNetwork
    }
}