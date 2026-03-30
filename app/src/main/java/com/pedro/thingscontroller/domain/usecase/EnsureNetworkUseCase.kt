package com.pedro.thingscontroller.domain.usecase

import com.pedro.thingscontroller.domain.repository.NetworkMonitor
import kotlinx.coroutines.flow.first

/**
 * Use case responsible for verifying whether the device currently
 * has an active internet connection.
 *
 * This is a utility use case meant to be composed into other use cases
 * that require network access before performing any operation, keeping
 * connectivity concerns centralized in a single place.
 *
 * @see NoNetworkException
 */
class EnsureNetworkUseCase(
    private val networkMonitor: NetworkMonitor
) {
    /**
     * Performs a one-shot check of the current network state.
     *
     * @return [Result.success] if the device is online,
     * or [Result.failure] wrapping a [NoNetworkException] if offline.
     */
    suspend operator fun invoke(): Boolean = networkMonitor.isOnline.first()
}

/**
 * Exception thrown when a network-dependent operation is attempted
 * without an active internet connection.
 *
 * @see EnsureNetworkUseCase
 */
class NoNetworkException : Exception("No internet connection")