package com.aramonp.workly.presentation.screen.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aramonp.workly.R
import com.aramonp.workly.domain.model.AuthState
import com.aramonp.workly.domain.model.User
import com.google.firebase.Timestamp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun SignUpScreen(onNavigateToLogIn: () -> Unit = {}, onNavigateToHome: () -> Unit = {}, viewModel: SignUpViewModel) {
    val authState = viewModel.authState.observeAsState()
    val name: String by viewModel.name.observeAsState("")
    val surname: String by viewModel.surname.observeAsState("")
    val username: String by viewModel.username.observeAsState("")
    val email: String by viewModel.email.observeAsState("")
    val password: String by viewModel.password.observeAsState("")

    var confirmPassword by remember { mutableStateOf("") }

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
            Spacer(modifier = Modifier.weight(1f))
            RegisterField(
                name,
                "Nombre",
                { value -> viewModel.onNameChange(value.trim()) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Text),
                VisualTransformation.None
            )
            RegisterField(
                surname,
                "Apellidos",
                { value -> viewModel.onSurnameChange(value.trim()) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Text),
                VisualTransformation.None
            )
            RegisterField(
                email,
                "Email",
                { value -> viewModel.onEmailChange(value.trim()) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Email),
                VisualTransformation.None
            )
            RegisterField(
                username,
                "Nombre de usuario",
                { value -> viewModel.onUsernameChange(value.trim()) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Text),
                VisualTransformation.None
            )
            RegisterField(
                password,
                "Contraseña",
                { value -> viewModel.onPasswordChange(value.trim()) },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Password),
                PasswordVisualTransformation()
            )
            RegisterField(
                confirmPassword,
                "Confirmar contraseña",
                { value -> confirmPassword = value.trim() },
                Modifier.fillMaxWidth(),
                KeyboardOptions(keyboardType = KeyboardType.Password),
                PasswordVisualTransformation(),
                password != confirmPassword
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
            TextButton(onClick = { onNavigateToLogIn() }) {
                Text(
                    text = "¿Ya tienes una cuenta? Inicia sesión",
                    color = Color.Gray
                )
            }
            /*
            Button(
                onClick = onNavigateToLogIn,
                colors = ButtonDefaults.buttonColors(Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión", color = Color.Gray)
            }
             */
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

@Composable
fun RegisterField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    keyboardOption: KeyboardOptions,
    visualTransformation: VisualTransformation,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        label = { Text(text = label) },
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardOptions = keyboardOption,
        visualTransformation = visualTransformation,
        isError = isError
    )
}