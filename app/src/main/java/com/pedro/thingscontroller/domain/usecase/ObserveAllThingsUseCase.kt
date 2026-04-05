package com.pedro.thingscontroller.domain.usecase

import com.pedro.thingscontroller.domain.model.thing.Thing
import com.pedro.thingscontroller.domain.repository.ThingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAllThingsUseCase @Inject constructor(
    private val thingRepository: ThingRepository
) {
    operator fun invoke(): Flow<Map<String, Thing>> = thingRepository.allThings
}