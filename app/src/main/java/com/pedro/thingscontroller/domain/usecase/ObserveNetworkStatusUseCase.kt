package com.pedro.thingscontroller.domain.usecase

import com.pedro.thingscontroller.domain.repository.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNetworkStatusUseCase @Inject constructor(
    private val networkMonitor: NetworkMonitor
) {
    operator fun invoke(): Flow<Boolean> {
        return networkMonitor.isOnline
    }
}