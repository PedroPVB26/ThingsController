package com.pedro.thingscontroller.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.thingscontroller.domain.model.thing.Thing
import com.pedro.thingscontroller.domain.usecase.GetNetworkStatusUseCase
import com.pedro.thingscontroller.domain.usecase.InitializeThingsUseCase
import com.pedro.thingscontroller.domain.usecase.ObserveAllThingsUseCase
import com.pedro.thingscontroller.domain.usecase.ObserveNetworkStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val initializeThingsUseCase: InitializeThingsUseCase,
    private val observeAllThingsUseCase: ObserveAllThingsUseCase,
    private val observeNetworkStatusUseCase: ObserveNetworkStatusUseCase
): ViewModel() {
    private val TAG = "HomeViewModel"


    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state : StateFlow<HomeUiState> = _state

    init {
        getThings()
    }

    private fun getThings(){
        viewModelScope.launch {
            initializeThingsUseCase()
            observeAllThingsUseCase().collect { things ->
                _state.value = HomeUiState.Success(things)
            }
        }
    }
}

sealed class HomeUiState{
    data object Loading: HomeUiState()

    data class Success(
        val things: Map<String, Thing>
    ): HomeUiState()

    data class Error(
        val message: String
    ): HomeUiState()
}


