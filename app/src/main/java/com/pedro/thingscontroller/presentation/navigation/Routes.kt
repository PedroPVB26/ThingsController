package com.pedro.thingscontroller.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object HomeRoute

@Serializable
data class ThingComponentsRoute(
    val thingName: String
)