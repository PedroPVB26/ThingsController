package com.pedro.thingscontroller.presentation.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.thingscontroller.domain.model.thing.Thing
import com.pedro.thingscontroller.domain.model.thing.ThingState
import com.pedro.thingscontroller.domain.model.thing.ThingStateStatus
import com.pedro.thingscontroller.presentation.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeUiState: HomeUiState,
    onSeeThingComponents: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(homeUiState){
            is HomeUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp)
                )
            }

            is HomeUiState.Success -> {
                homeUiState.things.keys.forEach { key ->
                    val thing = homeUiState.things[key]
                    val status = thing?.connection
                    Text("Thing: $key -  ${status?.status}")
                }
//                Text("Things: ${homeUiState.things.keys}")
            }

            is HomeUiState.Error -> {
                Text("Erro: ${homeUiState.message}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenLoadingPreview() {
    HomeScreen(
        homeUiState = HomeUiState.Loading,
        onSeeThingComponents = {}
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenSuccessPreview() {

    val things = mapOf(
        "lampada" to Thing(
            thingName = "lampada",
            userFriendlyName = "Lâmpada Sala",
            available = true,
            connection = ThingState(
                status = ThingStateStatus.CONNECTED,
                timestamp = System.currentTimeMillis()
            ),
            type = "ESP32"
        ),
        "geladeira" to Thing(
            thingName = "geladeira",
            userFriendlyName = "Geladeira Cozinha",
            available = false,
            connection = ThingState(
                status = ThingStateStatus.DISCONNECTED,
                timestamp = System.currentTimeMillis()
            ),
            type = "ESP32"
        ),
        "ventilador" to Thing(
            thingName = "ventilador",
            userFriendlyName = "Ventilador Quarto",
            available = true,
            connection = ThingState(
                status = ThingStateStatus.UNKNOWN,
                timestamp = System.currentTimeMillis()
            ),
            type = "Arduino"
        )
    )

    HomeScreen(
        homeUiState = HomeUiState.Success(things),
        onSeeThingComponents = {}
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenErrorPreview() {
    HomeScreen(
        homeUiState = HomeUiState.Error("Falha ao carregar dispositivos"),
        onSeeThingComponents = {}
    )
}