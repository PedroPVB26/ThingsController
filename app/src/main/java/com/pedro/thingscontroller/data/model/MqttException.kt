package com.pedro.thingscontroller.data.model

sealed class MqttException : Exception() {
    data object NotConnected : MqttException()
    data class SubscriptionFailed(val topic: String, override val cause: Throwable) : MqttException()
    data class PublishFailed(val topic: String, override val cause: Throwable) : MqttException()
}