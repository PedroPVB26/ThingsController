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
//import com.pedro.thingscontroller.data.datasource.impl.datastore.DataStoreTokenProvider
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var initializeThingsUseCase: InitializeThingsUseCase

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

            val result = loginUseCase("username", "senha")

            when (result) {
                is UseCaseResult.Success -> {
                    val tokens = result.data

                    Log.d("LOGIN_TEST", "Login sucesso")

                    Log.d("TOKEN", "AccessToken: ${tokens?.accessToken}")
                    Log.d("TOKEN", "RefreshToken: ${tokens?.refreshToken}")
                    Log.d("TOKEN", "IdToken: ${tokens?.idToken}")
                }
                is UseCaseResult.Failure -> {
                    Log.e("LOGIN_TEST", "Erro no login: $result")
                }
            }
        }

        lifecycleScope.launch {
            initializeThingsUseCase()
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