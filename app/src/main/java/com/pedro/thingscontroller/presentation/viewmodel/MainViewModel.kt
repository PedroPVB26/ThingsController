package com.pedro.thingscontroller.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.thingscontroller.domain.model.UseCaseResult
import com.pedro.thingscontroller.domain.usecase.CheckAuthStatusUseCase
import com.pedro.thingscontroller.domain.usecase.ObserveNetworkStatusUseCase
import com.pedro.thingscontroller.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkAuthStatusUseCase: CheckAuthStatusUseCase,
    private val observeNetworkStatusUseCase: ObserveNetworkStatusUseCase,
    private val signOutUseCase: SignOutUseCase
): ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Checking)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val isOnline = observeNetworkStatusUseCase()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        viewModelScope.launch {
            _authState.update {AuthState.Checking}

            when(val result = checkAuthStatusUseCase()){
                is UseCaseResult.Success -> {
                    _authState.update {AuthState.Authenticated}
                }
                else ->{
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _authState.update { AuthState.Unauthenticated }
        }
    }
}

sealed class AuthState {
    data object Checking : AuthState()
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
}