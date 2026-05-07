package com.pedro.thingscontroller.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.pedro.thingscontroller.presentation.navigation.AppNavHost
import com.pedro.thingscontroller.presentation.navigation.HomeRoute
import com.pedro.thingscontroller.presentation.navigation.LoginRoute
import com.pedro.thingscontroller.presentation.view.composables.MyTopAppBar
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
            val isOnline by viewModel.isOnline.collectAsStateWithLifecycle(initialValue = true)

            ThingsControllerTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                var showLogoutDialog by remember { mutableStateOf(false) }

                if (showLogoutDialog) {
                    AlertDialog(
                        onDismissRequest = { showLogoutDialog = false },
                        title = { Text("Sign Out") },
                        text = { Text("Are you sure you want to sign out of your account?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showLogoutDialog = false
                                    viewModel.signOut()
                                    navController.navigate(LoginRoute) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            ) {
                                Text("Yes")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showLogoutDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                var previousIsOnline by remember { mutableStateOf<Boolean?>(null) }

                LaunchedEffect(isOnline) {
                    if (previousIsOnline == false && isOnline) {
                        snackbarHostState.showSnackbar("Conexão restaurada")
                    }

                    if (previousIsOnline == true && !isOnline) {
                        snackbarHostState.showSnackbar("Sem conexão com a internet")
                    }

                    previousIsOnline = isOnline
                }

                Scaffold(
                    topBar = {
                        MyTopAppBar(
                            navController = navController,
                            onSignOut = {
                                showLogoutDialog = true
                            }
                        )
                    },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if(authState !is AuthState.Checking){
                        val startDestination = if(authState is AuthState.Authenticated) HomeRoute else LoginRoute

                        AppNavHost(
                            modifier = Modifier.padding(innerPadding),
                            navController = navController,
                            startDestination = startDestination,
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            }
        }

    }

}
