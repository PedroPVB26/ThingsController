package com.pedro.thingscontroller.presentation.view.composables

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pedro.thingscontroller.presentation.navigation.HomeRoute
import com.pedro.thingscontroller.presentation.navigation.LoginRoute
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme
import androidx.navigation.NavDestination.Companion.hasRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    modifier: Modifier = Modifier,
    navController: NavController
){
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val title = if (
        currentDestination?.hasRoute<HomeRoute>() == true ||
        currentDestination?.hasRoute<LoginRoute>() == true
    ) {
        "Things Controller"
    } else {
        "Thing Components"
    }


    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title
            )
        },
        navigationIcon = {
            if (
                currentDestination?.hasRoute<HomeRoute>() == false &&
                currentDestination?.hasRoute<LoginRoute>() == false
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            }
            else{
                IconButton(onClick = { }) {}
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Preview(name = "Light Top App Bar", showBackground = true)
@Preview(name = "Dark Top App Bar", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MyTopAppBarPreview(){
    ThingsControllerTheme() {
        Surface(color = MaterialTheme.colorScheme.background){
            val navController = rememberNavController()
            MyTopAppBar(navController = navController)
        }
    }
}