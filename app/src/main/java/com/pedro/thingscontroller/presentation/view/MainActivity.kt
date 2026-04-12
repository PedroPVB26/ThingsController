package com.pedro.thingscontroller.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pedro.thingscontroller.presentation.navigation.AppNavHost
import com.pedro.thingscontroller.presentation.navigation.HomeRoute
import com.pedro.thingscontroller.presentation.navigation.LoginRoute
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme
import com.pedro.thingscontroller.presentation.viewmodel.AuthState
import com.pedro.thingscontroller.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.authState.value is AuthState.Checking
        }

        enableEdgeToEdge()

        setContent {
            val authState by viewModel.authState.collectAsStateWithLifecycle()

            ThingsControllerTheme {

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if(authState !is AuthState.Checking){
                        val startDestination = if(authState is AuthState.Authenticated) HomeRoute else LoginRoute
                        AppNavHost(
                            modifier = Modifier.padding(innerPadding),
                            startDestination = startDestination
                        )
                    }
                }
            }
        }

    }

}
