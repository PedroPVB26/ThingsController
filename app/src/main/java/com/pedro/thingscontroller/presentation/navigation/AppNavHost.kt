package com.pedro.thingscontroller.presentation.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pedro.thingscontroller.presentation.view.screen.HomeScreen
import com.pedro.thingscontroller.presentation.view.screen.LoginScreen
import com.pedro.thingscontroller.presentation.view.screen.ThingComponentsScreen
import com.pedro.thingscontroller.presentation.viewmodel.HomeViewModel
import com.pedro.thingscontroller.presentation.viewmodel.LoginViewModel
import com.pedro.thingscontroller.presentation.viewmodel.ThingComponentsViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any = LoginRoute,
    snackbarHostState: SnackbarHostState
){

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ){
        composable<LoginRoute>{
            val loginViewModel: LoginViewModel = hiltViewModel()
            val loginUiState by loginViewModel.state.collectAsStateWithLifecycle()

            LoginScreen(
                loginUiState = loginUiState,
                onLoginClick = {email, password ->
                    loginViewModel.login(email, password)
                },
                onNavigateToHome = {
                    navController.navigate(HomeRoute){
                        popUpTo<LoginRoute> {inclusive = true} // clears login from back stack
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<HomeRoute>{
            val homeViewModel: HomeViewModel = hiltViewModel()
            val homeUiState by homeViewModel.state.collectAsStateWithLifecycle()

            HomeScreen(
                homeUiState = homeUiState,
                onSeeThingComponents = {thingName ->
                    navController.navigate(ThingComponentsRoute(thingName)){
                        launchSingleTop = true
                    }
                },
                snackbarHostState = snackbarHostState

            )
        }

        composable<ThingComponentsRoute>{
            val thingComponentsViewModel: ThingComponentsViewModel = hiltViewModel()
            val componentsUiState by thingComponentsViewModel.state.collectAsStateWithLifecycle()

            ThingComponentsScreen(
                componentsUiState = componentsUiState,
                snackbarHostState = snackbarHostState,
                onSendCommand = {thingName, command ->
                    thingComponentsViewModel.sendCommand(thingName, command)
                }
            )
        }
    }
}