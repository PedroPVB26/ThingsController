package com.pedro.thingscontroller.domain.model

sealed class UseCaseResult<out T> {
    data class Success<T>(val data: T): UseCaseResult<T>()
    sealed class Failure: UseCaseResult<Nothing>(){
        data object NoNetwork: Failure()
        data object Timeout: Failure()
        data class ThingError(val exception: ThingException) : Failure()
        data class Unknown(val cause: Throwable) : Failure()
    }
}