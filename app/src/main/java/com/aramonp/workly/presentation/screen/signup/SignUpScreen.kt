package com.aramonp.workly.presentation.screen.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aramonp.workly.R

@Preview
@Composable
fun SignUpScreen(onNavigateToLogIn: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(start = 10.dp, end = 10.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        RegisterField(
            "Nombre",
            {},
            Modifier.fillMaxWidth(),
            KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(9.dp))
        RegisterField(
            "Apellidos",
            {},
            Modifier.fillMaxWidth(),
            KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(9.dp))
        RegisterField(
            "Email",
            {},
            Modifier.fillMaxWidth(),
            KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(9.dp))
        RegisterField(
            "Nombre de usuario",
            {},
            Modifier.fillMaxWidth(),
            KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(9.dp))
        RegisterField(
            "Contraseña",
            {},
            Modifier.fillMaxWidth(),
            KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(9.dp))
        Button(
            onClick = {},
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
        Button(
            onClick = onNavigateToLogIn,
            colors = ButtonDefaults.buttonColors(Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión", color = Color.Gray)
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
        Spacer(modifier = Modifier.height(8.dp))
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
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    keyboardOption: KeyboardOptions) {
    OutlinedTextField(
        value = "",
        label = { Text(text = value) },
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardOptions = keyboardOption
    )
}