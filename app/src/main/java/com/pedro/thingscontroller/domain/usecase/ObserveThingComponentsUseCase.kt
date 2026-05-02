package com.pedro.thingscontroller.domain.usecase

import com.pedro.thingscontroller.domain.model.component.Component
import com.pedro.thingscontroller.domain.repository.ThingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveThingComponentsUseCase @Inject constructor(
    private val thingRepository: ThingRepository
) {
    operator fun invoke(thingName: String): Flow<List<Component>>{
        return thingRepository.allThingsComponents.map { it[thingName] ?: emptyList()}
    }
}