package com.pedro.thingscontroller.presentation.view.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.thingscontroller.domain.model.component.Component
import com.pedro.thingscontroller.domain.model.component.ComponentActionDescriptor
import com.pedro.thingscontroller.domain.model.component.ComponentState
import com.pedro.thingscontroller.domain.model.component.ComponentPrecision
import com.pedro.thingscontroller.domain.model.component.instance.LedInstance
import com.pedro.thingscontroller.domain.model.component.instance.TemperatureUmidityInstance
import com.pedro.thingscontroller.domain.model.command.ThingCommand
import com.pedro.thingscontroller.domain.model.component.ComponentType
import com.pedro.thingscontroller.domain.model.thing.Thing
import com.pedro.thingscontroller.domain.model.thing.ThingState
import com.pedro.thingscontroller.domain.model.thing.ThingStateStatus
import com.pedro.thingscontroller.presentation.view.composables.LedSection
import com.pedro.thingscontroller.presentation.view.composables.TemperatureHumiditySensorSection
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme
import com.pedro.thingscontroller.presentation.viewmodel.ComponentsState
import com.pedro.thingscontroller.presentation.viewmodel.ComponentsUiState

@Composable
fun ThingComponentsScreen(
    componentsUiState: ComponentsUiState,
    snackbarHostState: SnackbarHostState,
    onSendCommand: (String, ThingCommand) -> Unit
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ){
            componentsUiState.thing?.let { thing ->
                val isConnected = thing.connection.status == ThingStateStatus.CONNECTED
                val statusColor = if(isConnected)
                    MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = thing.userFriendlyName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = thing.type,
                        style = MaterialTheme.typography.headlineSmall,
                    )

                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = CircleShape,
                        color = statusColor
                    ) { }

                    Text(
                        text = if (isConnected) "CONNECTED" else "DISCONNECTED",
                        style = MaterialTheme.typography.bodyMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        }

        when(componentsUiState.componentsState){
            is ComponentsState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp)
                    )

                    Text(
                        text = "Loading Components..."
                    )
                }

            }

            is ComponentsState.Success -> {
                val components = componentsUiState.componentsState.components
                val thing = componentsUiState.thing
                val isThingConnected = componentsUiState.thing!!.connection.status == ThingStateStatus.CONNECTED
                val canInteract = isThingConnected && componentsUiState.isOnline


                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    components.forEach { component ->
                        Spacer(modifier = Modifier.height(24.dp))
                        when(component.type){
                            ComponentType.LED -> {
                                ComponentHeader("LEDs")
                                LedSection(
                                    led = component,
                                    canInteract = canInteract,
                                    onActionClick = {command ->
                                        onSendCommand(thing.thingName, command)
                                    }
                                )
                            }
                            ComponentType.TEMPERATURE_UMIDITY_SENSOR -> {
                                ComponentHeader("Temperature & Humidity Sensors")
                                TemperatureHumiditySensorSection(
                                    sensor = component
                                )
                            }

                            ComponentType.BUZZER -> {
                                ComponentHeader("Buzzers")
                      ''      }

                            else -> {}
                        }
                    }
                }
                Text(components.toString())
            }

            is ComponentsState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "Error: ${componentsUiState.componentsState.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }




    }
}

@Composable
private fun ComponentHeader(text: String){
    Column {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
    }
}


@Preview(name = "Light Mode Loading", showBackground = true)
@Preview(name = "Dark Mode Loading", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ComponentsScreenLoadingPreview(){
    ThingsControllerTheme() {
        Surface(color = MaterialTheme.colorScheme.background){
            val snackbarHostState = remember { SnackbarHostState() }


            ThingComponentsScreen(
                componentsUiState = ComponentsUiState(
                    Thing(
                        thingName = "lampada",
                        userFriendlyName = "Cozinha",
                        available = true,
                        connection = ThingState(
                            status = ThingStateStatus.CONNECTED,
                            timestamp = System.currentTimeMillis()
                        ),
                        type = "ESP32"
                    ),
                    ComponentsState.Loading,
                    true,
                ),
                snackbarHostState = snackbarHostState,
                onSendCommand = {_, _ -> }
            )
        }
    }
}

@Preview(name = "Light Mode Success", showBackground = true)
@Preview(name = "Dark Mode Success", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ComponentsScreenSuccessPreview() {
    val mockThing = Thing(
        thingName = "esp32_A2F00F4134B1",
        userFriendlyName = "Quarto",
        available = true,
        connection = ThingState(
            status = ThingStateStatus.CONNECTED,
            timestamp = System.currentTimeMillis()
        ),
        type = "ESP32"
    )

    val mockComponents = listOf(
        Component(
            type = ComponentType.LED,
            actions = listOf(
                ComponentActionDescriptor.Led.ON,
                ComponentActionDescriptor.Led.OFF,
                ComponentActionDescriptor.Led.BLINK
            ),
            instances = listOf(
                LedInstance(
                    componentId = "greenLed",
                    available = true,
                    state = ComponentState.LedState.ON,
                    updatedAt = System.currentTimeMillis(),
                    pendingRequestId = null
                ),
                LedInstance(
                    componentId = "redLed",
                    available = true,
                    state = ComponentState.LedState.OFF,
                    updatedAt = System.currentTimeMillis(),
                    pendingRequestId = null
                )
            )
        ),
        Component(
            type = ComponentType.TEMPERATURE_UMIDITY_SENSOR,
            actions = emptyList(),
            instances = listOf(
                TemperatureUmidityInstance(
                    componentId = "dht11",
                    available = true,
                    state = ComponentState.TemperatureHumidityState(
                        temperature = 24.5,
                        humidity = 60.0,
                        updatedAt = System.currentTimeMillis()
                    ),
                    updatedAt = System.currentTimeMillis(),
                    pendingRequestId = null,
                    precision = ComponentPrecision.TemperatureUmiditySensorPrecision(1, 0)
                )
            )
        )
    )

    ThingsControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val snackbarHostState = remember { SnackbarHostState() }
            ThingComponentsScreen(
                componentsUiState = ComponentsUiState(
                    thing = mockThing,
                    componentsState = ComponentsState.Success(mockComponents),
                    isOnline = true
                ),
                snackbarHostState = snackbarHostState,
                onSendCommand = { _, _ -> }
            )
        }
    }
}
