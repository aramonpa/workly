package com.aramonp.workly.presentation.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aramonp.workly.R
import com.aramonp.workly.domain.model.AuthState
import kotlinx.coroutines.launch

@Composable
fun LogInScreen(onNavigateToSignUp: () -> Unit, onNavigateToHome: () -> Unit, viewModel: LogInViewModel) {
    val authState = viewModel.authState.collectAsState()
    val email: String by viewModel.email.collectAsState()
    val password: String by viewModel.password.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            Row(
            ) {
                Text("Workly", fontSize = 50.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
            ) {
                Text("Crea una cuenta o inicia sesión para empezar a gestionar tus equipos.")
            }
            Spacer(modifier = Modifier.height(8.dp))
            LogInField(
                email,
                "Email",
                { value -> viewModel.onEmailChange(value) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Email),
                VisualTransformation.None
            )
            LogInField(
                password,
                "Contraseña",
                { value -> viewModel.onPasswordChange(value) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Password),
                PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.logIn(email, password)
                    }
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }
            TextButton(onClick = { onNavigateToSignUp() }) {
                Text(
                    text = "¿No tienes una cuenta? Regístrate",
                    color = Color.Gray
                )
            }

            if (authState.value is AuthState.Error) {
                val errorMessage = (authState.value as AuthState.Error).message
                Text("Error: $errorMessage", color = Color.Red)
            }
            /*
            Button(
                onClick = onNavigateToSignUp,
                colors = ButtonDefaults.buttonColors(Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse", color = Color.Gray)
            }
             */
            Spacer(modifier = Modifier.weight(1f))
            SocialButton({}, R.drawable.apple, "Continuar con Apple")
            Spacer(modifier = Modifier.height(8.dp))
            SocialButton({}, R.drawable.google, "Continuar con Google")
            Spacer(modifier = Modifier.height(8.dp))
            SocialButton({}, R.drawable.facebook, "Continuar con Facebook")

        }
    }

    /*
    TODO:
     Check if is better to use navController directly
     navController.navigate(Route.SignUpScreen.route)
     */
    when (authState.value) {
        is AuthState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(64.dp)
                        .aspectRatio(1f)
                )
            }
        }
        is AuthState.Success -> {
            LaunchedEffect(Unit) {
                onNavigateToHome()
            }
        }
        else -> Unit
    }
}

@Composable
fun LogInContent() {

}

@Composable
fun LogInField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    keyboardOption: KeyboardOptions,
    visualTransformation: VisualTransformation
) {
    OutlinedTextField(
        value = value,
        label = { Text(text = label) },
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardOptions = keyboardOption,
        visualTransformation = visualTransformation
    )
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