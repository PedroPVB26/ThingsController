package com.pedro.thingscontroller.domain.usecase

import com.pedro.thingscontroller.domain.model.ThingException
import com.pedro.thingscontroller.domain.model.UseCaseResult
import com.pedro.thingscontroller.domain.repository.ThingRepository
import javax.inject.Inject

class InitializeThingsUseCase @Inject constructor(
    private val thingRepository: ThingRepository,
    private val ensureNetworkUseCase: EnsureNetworkUseCase
) {
    suspend operator fun invoke(): UseCaseResult<Unit>{
        ensureNetworkUseCase().let {
            if(it is UseCaseResult.Failure.NoNetwork) return it
        }

        return try {
            thingRepository.initialize()
            UseCaseResult.Success(Unit)
        } catch (e: ThingException) {
            UseCaseResult.Failure.ThingError(e)
        } catch (e: Exception) {
            UseCaseResult.Failure.Unknown(e)
        }
    }
}