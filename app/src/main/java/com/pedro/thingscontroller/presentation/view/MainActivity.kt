package com.pedro.thingscontroller.presentation.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pedro.thingscontroller.domain.model.UseCaseResult
import com.pedro.thingscontroller.domain.model.exception.MyAuthException
import com.pedro.thingscontroller.domain.usecase.InitializeThingsUseCase
import com.pedro.thingscontroller.domain.usecase.LoginUseCase
import com.pedro.thingscontroller.domain.repository.ThingRepository
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme
import com.pedro.thingscontroller.presentation.viewmodel.LoginUiState
import com.pedro.thingscontroller.presentation.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var initializeThingsUseCase: InitializeThingsUseCase

    @Inject
    lateinit var thingRepository: ThingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThingsControllerTheme {
                val loginState by loginViewModel.state.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var email by remember { mutableStateOf("") }
                    var passsword by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = email,
                            onValueChange = {email = it},
                            label = {Text("Email")},
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = passsword,
                            onValueChange = {passsword = it},
                            label = {Text("Password")},
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                loginViewModel.login(email, passsword)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Login")
                        }

                        if(loginState is LoginUiState.Loading){
                            CircularProgressIndicator(
                                modifier = Modifier.size(60.dp),
                                strokeWidth = 2.dp
                            )
                        }

                        Text(
                            text = when(loginState){
                                !is  LoginUiState.Success -> loginState.toString()
                                else -> "Logado com sucesso"
                            }
                        )
                    }
                }
            }
        }
        // 3. Monitoramento contínuo (Flows)
        /*lifecycleScope.launch {
            thingRepository.allThings.collectLatest { things ->
                Log.d("THINGS_TEST", "3. Update Things: ${things.size} found")
                things.forEach { (id, thing) ->
                    Log.d("THINGS_TEST", "   - Thing: $id (ID real no model), Name: ${thing.userFriendlyName}, Status: ${thing.connection.status}")
                }
            }
        }
*/


        /*lifecycleScope.launch {
            thingRepository.allThingsComponents.collectLatest { componentsMap ->
                if (componentsMap.isEmpty()) {
                    Log.d("THINGS_TEST", "3. Update Components: Map is empty")
                } else {
                    Log.d("THINGS_TEST", "3. Update Components: ${componentsMap.size} things with components")
                    componentsMap.forEach { (thingId, components) ->
                        components.forEach { component ->
                            component.instances.forEach { instance ->
                                Log.d("THINGS_TEST", "   - Thing: $thingId | Comp: ${instance.componentId} | State: ${instance.state}")
                            }
                        }
                    }
                }
            }
        }*/
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ThingsControllerTheme {
        Greeting("Android")
    }
}