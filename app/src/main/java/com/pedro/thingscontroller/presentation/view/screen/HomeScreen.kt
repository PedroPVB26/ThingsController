package com.pedro.thingscontroller.presentation.view.screen

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.thingscontroller.domain.model.thing.Thing
import com.pedro.thingscontroller.domain.model.thing.ThingState
import com.pedro.thingscontroller.domain.model.thing.ThingStateStatus
import com.pedro.thingscontroller.presentation.view.composables.ThingComposable
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme
import com.pedro.thingscontroller.presentation.viewmodel.HomeUiState

@Composable
fun HomeScreen(
//    modifier: Modifier = Modifier,
    homeUiState: HomeUiState,
    onSeeThingComponents: (String) -> Unit,
    snackbarHostState: SnackbarHostState? = null
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .pointerInput(Unit){
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        verticalArrangement = when(homeUiState) {
            is HomeUiState.Loading -> Arrangement.Center
            is HomeUiState.Error -> Arrangement.Center
            is HomeUiState.Success -> Arrangement.Top
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(homeUiState){
            is HomeUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = "Loading Things..."
                )
            }

            is HomeUiState.Success -> {
                var searchQuery by remember { mutableStateOf("") }
                val filteredThings = remember(searchQuery, homeUiState.things) {
                    homeUiState.things.filter { (name, thing) ->
                        thing.userFriendlyName.contains(searchQuery, true) ||
                                thing.userFriendlyName.contains(searchQuery, true)
                    }
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Things",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${filteredThings.size} found",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {searchQuery = it},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        placeholder = { Text("Search things...") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search icon")
                        },
                        trailingIcon = {
                            if(searchQuery != ""){
                                IconButton(
                                    onClick = {searchQuery = ""}
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear field icon")
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    if(filteredThings.isNotEmpty()){
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = filteredThings.toList(),
                                key = {it.first}
                            ){(thingName, thing) ->
                                ThingComposable(
                                    thing = thing,
                                    onSeeComponents = {onSeeThingComponents(thingName)}
                                )
                            }

                        }
                    }else{
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No things found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "We couldn't find anything matching \"$searchQuery\".",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Try using a different name or ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )

                                TextButton(
                                    onClick = { searchQuery = "" },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "clear the search",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }

            }

            is HomeUiState.Error -> {
                Text("Error: ${homeUiState.message}")
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenLoadingPreview() {
    ThingsControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeScreen(
                homeUiState = HomeUiState.Loading,
                onSeeThingComponents = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenSuccessPreview() {

    val things = mapOf(
        "lampada" to Thing(
            thingName = "lampada",
            userFriendlyName = "Cozinha",
            available = true,
            connection = ThingState(
                status = ThingStateStatus.CONNECTED,
                timestamp = System.currentTimeMillis()
            ),
            type = "ESP32"
        ),
        "geladeira" to Thing(
            thingName = "geladeira",
            userFriendlyName = "Quarto",
            available = false,
            connection = ThingState(
                status = ThingStateStatus.DISCONNECTED,
                timestamp = System.currentTimeMillis()
            ),
            type = "ESP32"
        ),
        "ventilador" to Thing(
            thingName = "ventilador",
            userFriendlyName = "Sala",
            available = true,
            connection = ThingState(
                status = ThingStateStatus.UNKNOWN,
                timestamp = System.currentTimeMillis()
            ),
            type = "Arduino"
        )
    )

    ThingsControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeScreen(
                homeUiState = HomeUiState.Success(things),
                onSeeThingComponents = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenErrorPreview() {
    ThingsControllerTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeScreen(
                homeUiState = HomeUiState.Error("Falha ao carregar dispositivos"),
                onSeeThingComponents = {}
            )
        }
    }
}