package com.pedro.thingscontroller.domain.usecase

import com.pedro.thingscontroller.domain.model.thing.Thing
import com.pedro.thingscontroller.domain.repository.ThingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveThingUseCase @Inject constructor(
    private val thingRepository: ThingRepository
){
    operator fun invoke(thingName: String): Flow<Thing?> = thingRepository.observeThing(thingId = thingName)
}