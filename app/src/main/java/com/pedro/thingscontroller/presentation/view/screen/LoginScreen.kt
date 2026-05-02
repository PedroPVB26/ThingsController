package com.pedro.thingscontroller.presentation.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pedro.thingscontroller.presentation.view.ui.theme.ThingsControllerTheme
import com.pedro.thingscontroller.presentation.viewmodel.LoginUiState

@Composable
fun LoginScreen(
//    modifier: Modifier = Modifier,
    loginUiState: LoginUiState,
    onLoginClick: (String, String) -> Unit,
    onNavigateToHome: () -> Unit
){

    var email by remember { mutableStateOf("") }
    var passsword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(loginUiState) {
        if(loginUiState is LoginUiState.Success){
            onNavigateToHome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding(), // adiciona automaticamente um espaço na parte de baixo quando o teclado aparece.
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Welcome",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = {Text("Email")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passsword,
            onValueChange = {passsword = it},
            label = {Text("Password")},
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility  else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(8.dp))

        if(loginUiState is LoginUiState.Error){
            Text(
                text = loginUiState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {onLoginClick(email, passsword)},
            enabled = loginUiState != LoginUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if(loginUiState == LoginUiState.Loading){
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }else{
                Text("Login")
            }
        }
    }
}

@Preview(name = "Idle", showBackground = true)
@Composable
private fun LoginScreenIdlePreview() {
    ThingsControllerTheme() {
        LoginScreen(
            loginUiState = LoginUiState.Idle,
            onLoginClick = {email, password -> },
            onNavigateToHome = {}
        )
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun LoginScreenLoadingPreview() {
    ThingsControllerTheme() {
        LoginScreen(
            loginUiState = LoginUiState.Loading,
            onLoginClick = {email, password -> },
            onNavigateToHome = {}
        )
    }
}

@Preview(name = "Error", showBackground = true)
@Composable
private fun LoginScreenErrorPreview() {
    ThingsControllerTheme() {
        LoginScreen(
            loginUiState = LoginUiState.Error("Invalid Credentials"),
            onLoginClick = {email, password -> },
            onNavigateToHome = {}
        )
    }
}
