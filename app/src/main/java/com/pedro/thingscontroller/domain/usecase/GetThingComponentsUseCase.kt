package com.pedro.thingscontroller.domain.usecase

import android.util.Log
import com.pedro.thingscontroller.domain.model.UseCaseResult
import com.pedro.thingscontroller.domain.repository.ThingRepository
import javax.inject.Inject

class GetThingComponentsUseCase @Inject constructor(
    private val getNetworkStatusUseCase: GetNetworkStatusUseCase,
    private val thingRepository: ThingRepository
) {
    suspend operator fun invoke(thingName: String): UseCaseResult<Unit>{
        getNetworkStatusUseCase().let {
            if(it is UseCaseResult.Failure.NoNetwork) return it
        }

        return try {
            thingRepository.getThingComponents(thingName)
            Log.i("GetThingComponentsUseCase", "invokedo")
            UseCaseResult.Success(Unit)
        } catch (e: Exception) {
            UseCaseResult.Failure.Unknown(e)
        }
    }
}