package com.pedro.thingscontroller.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.pedro.thingscontroller.domain.model.command.ThingCommand
import com.pedro.thingscontroller.domain.model.component.Component
import com.pedro.thingscontroller.domain.model.thing.Thing
import com.pedro.thingscontroller.domain.usecase.GetThingComponentsUseCase
import com.pedro.thingscontroller.domain.usecase.ObserveNetworkStatusUseCase
import com.pedro.thingscontroller.domain.usecase.ObserveThingComponentsUseCase
import com.pedro.thingscontroller.domain.usecase.ObserveThingUseCase
import com.pedro.thingscontroller.domain.usecase.SendCommandUseCase
import com.pedro.thingscontroller.presentation.navigation.ThingComponentsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThingComponentsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeThingUseCase: ObserveThingUseCase,
    private val getThingComponentsUseCase: GetThingComponentsUseCase,
    private val observeThingComponentsUseCase: ObserveThingComponentsUseCase,
    private val observeNetworkStatusUseCase: ObserveNetworkStatusUseCase,
    private val sendCommandUseCase: SendCommandUseCase
): ViewModel(){
    private val TAG = "ThingComponentsViewModel"

    private val route = savedStateHandle.toRoute<ThingComponentsRoute>()
    val thingName = route.thingName

    private val _state = MutableStateFlow(ComponentsUiState(thing = null, isOnline = true))
    val state: StateFlow<ComponentsUiState> = _state

    init {
        observeThing()
        observeNetwork()
        getComponents()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            observeNetworkStatusUseCase().collect { isOnline ->
                _state.update { currentState ->
                    currentState.copy(isOnline = isOnline)
                }
            }
        }
    }

    private fun observeThing(){
        viewModelScope.launch {
            observeThingUseCase(thingName).collect {thing ->
                _state.update { currentState ->
                    currentState.copy(thing = thing)
                }
            }
        }
    }

    private fun getComponents(){
        viewModelScope.launch {
            getThingComponentsUseCase(thingName)
            observeThingComponentsUseCase(thingName).collect { it ->
                _state.update { currentState ->
                    currentState.copy(componentsState = ComponentsState.Success(it))
                }
            }
        }
    }

    fun sendCommand(thingName: String, command: ThingCommand){
        viewModelScope.launch {
            sendCommandUseCase(thingName, command)
        }
    }
}


data class ComponentsUiState(
    val thing: Thing?,
    val componentsState: ComponentsState = ComponentsState.Loading,
    val isOnline: Boolean
)

sealed class ComponentsState {
    data object Loading : ComponentsState()
    data class Success(val components: List<Component>) : ComponentsState()
    data class Error(val message: String) : ComponentsState()
}
