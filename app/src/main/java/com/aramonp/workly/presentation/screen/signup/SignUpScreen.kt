package com.aramonp.workly.presentation.screen.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aramonp.workly.R
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.presentation.component.CircularProgress
import com.aramonp.workly.presentation.component.OutlinedFormTextField
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(onNavigateToLogIn: () -> Unit = {}, onNavigateToHome: () -> Unit = {}, viewModel: SignUpViewModel = hiltViewModel()) {
    val authState = viewModel.authState.collectAsState()
    val signUpFormState = viewModel.signUpFormState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            OutlinedFormTextField(
                signUpFormState.value.name,
                "Nombre",
                { value -> viewModel.onNameChange(value.trim()) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = signUpFormState.value.nameError != null,
                errorMessage = signUpFormState.value.nameError
            )
            OutlinedFormTextField(
                signUpFormState.value.surname,
                "Apellidos",
                { value -> viewModel.onSurnameChange(value.trim()) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = signUpFormState.value.surnameError != null,
                errorMessage = signUpFormState.value.surnameError
            )
            OutlinedFormTextField(
                signUpFormState.value.email,
                "Email",
                { value -> viewModel.onEmailChange(value.trim()) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = signUpFormState.value.emailError != null,
                errorMessage = signUpFormState.value.emailError
            )
            OutlinedFormTextField(
                signUpFormState.value.username,
                "Nombre de usuario",
                { value -> viewModel.onUsernameChange(value.trim()) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = signUpFormState.value.usernameError != null,
                errorMessage = signUpFormState.value.usernameError
            )
            OutlinedFormTextField(
                signUpFormState.value.password,
                "Contraseña",
                { value -> viewModel.onPasswordChange(value.trim()) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Password),
                PasswordVisualTransformation(),
                isError = signUpFormState.value.passwordError != null,
                errorMessage = signUpFormState.value.passwordError
            )
            OutlinedFormTextField(
                signUpFormState.value.repeatedPassword,
                "Confirmar contraseña",
                { value -> viewModel.onRepeatedPasswordChange(value.trim()) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Password),
                PasswordVisualTransformation(),
                isError = signUpFormState.value.repeatedPasswordError != null,
                errorMessage = signUpFormState.value.repeatedPasswordError
            )
            Spacer(modifier = Modifier.height(9.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.signUp()
                    }
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = { onNavigateToLogIn() }
            ) {
                Text(
                    text = "¿Ya tienes una cuenta? Inicia sesión",
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            SocialButton(
                {},
                R.drawable.apple,
                "Continuar con Apple"
            )
            Spacer(modifier = Modifier.height(8.dp))
            SocialButton(
                {},
                R.drawable.google,
                "Continuar con Google"
            )
            Spacer(modifier = Modifier.height(8.dp))
            SocialButton(
                {},
                R.drawable.facebook,
                "Continuar con Facebook"
            )
        }
    }

    /*
    TODO:
     Check if is better to use navController directly
     navController.navigate(Route.SignUpScreen.route)
     */
    when (authState.value) {
        is AuthState.Loading -> {
            CircularProgress()
        }
        is AuthState.Success -> {
            LaunchedEffect(Unit) {
                onNavigateToHome()
            }
        }
        is AuthState.Error -> {
            val errorMessage = (authState.value as AuthState.Error).message
            Text("Error: $errorMessage", color = Color.Red)
        }
        else -> Unit
    }
}

@Composable
fun SocialButton(onClick: () -> Unit, drawableId: Int, value: String) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.LightGray, RoundedCornerShape(5.dp))
    ) {
        Image(
            painter = painterResource(drawableId),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 5.dp)
                .size(16.dp)
        )
        Text(value, color = Color.Black)
    }
}