package com.pedro.thingscontroller.presentation.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.pedro.thingscontroller.domain.model.UseCaseResult
import com.pedro.thingscontroller.domain.usecase.InitializeThingsUseCase
import com.pedro.thingscontroller.domain.usecase.LoginUseCase
import com.pedro.thingscontroller.domain.repository.ThingRepository
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var initializeThingsUseCase: InitializeThingsUseCase

    @Inject
    lateinit var thingRepository: ThingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThingsControllerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        lifecycleScope.launch {
            // 1. Login
            val loginResult = loginUseCase("username", "senha")
            when (loginResult) {
                is UseCaseResult.Success -> {
                    val tokens = loginResult.data
                    Log.d("THINGS_TEST", "1. Login Sucesso")
                    Log.d("THINGS_TEST", "   - ID Token: ${tokens?.idToken}")
                    Log.d("THINGS_TEST", "   - Access Token: ${tokens?.accessToken}")
                    
                    // 2. Só inicializa após o login ter sucesso
                    val initResult = initializeThingsUseCase()
                    Log.d("THINGS_TEST", "2. FetchInitialThings Resultado: $initResult")
                }
                is UseCaseResult.Failure -> {
                    Log.e("THINGS_TEST", "1. Login Erro: $loginResult")
                }
            }
        }

        // 3. Monitoramento contínuo (Flows)
        lifecycleScope.launch {
            thingRepository.allThings.collectLatest { things ->
                Log.d("THINGS_TEST", "3. Update Things: ${things.size} found")
                things.forEach { (id, thing) ->
                    Log.d("THINGS_TEST", "   - Thing: $id (ID real no model), Name: ${thing.userFriendlyName}, Status: ${thing.connection.status}")
                }
            }
        }

        lifecycleScope.launch {
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
        }
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