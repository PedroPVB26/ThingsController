package com.pedro.thingscontroller.domain.model.exception

sealed class MyAuthException : Exception() {

    class InvalidCredentials : MyAuthException()

    data class Unknown(val original: Throwable) : MyAuthException()
}