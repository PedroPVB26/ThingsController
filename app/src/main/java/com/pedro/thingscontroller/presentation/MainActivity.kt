package com.pedro.thingscontroller.presentation

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
import com.pedro.thingscontroller.data.datasource.impl.datastore.DataStoreTokenProvider
import com.pedro.thingscontroller.presentation.ui.theme.ThingsControllerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
            val provider = DataStoreTokenProvider(applicationContext)

            provider.saveToken("meu_token_teste")

            val token = provider.getToken()
            Log.d("TEST_TOKEN", "Token recuperado: $token")

            provider.clearToken()

            val tokenAfterClear = provider.getToken()
            Log.d("TEST_TOKEN", "Após limpar: $tokenAfterClear")
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