package com.pedro.thingscontroller.domain.model.exception

sealed class MyAuthException : Exception() {

    class InvalidCredentials : MyAuthException()

    class SessionExperied: MyAuthException()

    data class Unknown(val original: Throwable) : MyAuthException()
}