package com.pedro.thingscontroller.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedro.thingscontroller.domain.model.Tokens
import com.pedro.thingscontroller.domain.model.UseCaseResult
import com.pedro.thingscontroller.domain.model.exception.MyAuthException
import com.pedro.thingscontroller.domain.usecase.InitializeThingsUseCase
import com.pedro.thingscontroller.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor (
    private val loginUseCase: LoginUseCase,
//    private val initializeThingsUseCase: InitializeThingsUseCase
): ViewModel() {
    private val TAG = "LoginViewModel"

    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val state: StateFlow<LoginUiState> = _state

    fun login(email: String, password: String){
        viewModelScope.launch {
            _state.update { LoginUiState.Loading }

            val result = loginUseCase(email, password)

            when(result){
                is UseCaseResult.Success -> {
                    val tokens = result.data
                    _state.update{ LoginUiState.Success(tokens) }

//                    initializeThingsUseCase()
                }

                is UseCaseResult.Failure.AuthError -> {
                    val message = when(result.exception){
                        is MyAuthException.InvalidCredentials -> "Invalid credentials"
                        else -> "Login error, please try later"
                    }

                    _state.update { LoginUiState.Error(message) }
                }

                is UseCaseResult.Failure.NoNetwork -> {
                    _state.update { LoginUiState.Error("You're offline") }

                }

                else -> {
                    Log.e(TAG, "login $result")
                    _state.update { LoginUiState.Error("Unexpected error") }
                }
            }
        }
    }
}

sealed class LoginUiState {

    data object Idle : LoginUiState()

    data object Loading : LoginUiState()

    data class Success(val tokens: Tokens?) : LoginUiState()

    data class Error(val message: String) : LoginUiState()
}