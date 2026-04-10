package com.pedro.thingscontroller.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pedro.thingscontroller.presentation.view.screen.LoginScreen
import com.pedro.thingscontroller.presentation.viewmodel.LoginViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Any = LoginRoute
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
                modifier = modifier,
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
    }
}