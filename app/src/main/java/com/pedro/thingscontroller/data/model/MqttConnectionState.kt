package com.pedro.thingscontroller.data.model

import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback

sealed class MqttConnectionState{
    data object Connected : MqttConnectionState()
    data object Connecting : MqttConnectionState()
    data object Disconnected : MqttConnectionState()
    data class Error(val cause: Throwable?) : MqttConnectionState()
}

fun AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.toMqttConnectionState(
    throwable: Throwable?
): MqttConnectionState = when (this) {
    AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected ->
        MqttConnectionState.Connected
    AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connecting ->
        MqttConnectionState.Connecting
    AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting ->
        MqttConnectionState.Connecting
    AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost ->
        MqttConnectionState.Error(throwable)
    else ->
        MqttConnectionState.Disconnected
}