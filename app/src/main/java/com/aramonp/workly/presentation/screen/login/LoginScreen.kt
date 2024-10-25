package com.aramonp.workly.presentation.screen.login

import android.media.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aramonp.workly.R

@Preview
@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(start = 10.dp, end = 10.dp),
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
        LoginField("Correo electrónico")
        Spacer(modifier = Modifier.height(8.dp))
        LoginField("Contraseña")
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {},
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse", color = Color.Gray)
        }
        Spacer(modifier = Modifier.weight(1f))
        SocialButton({}, R.drawable.apple, "Continuar con Apple")
        Spacer(modifier = Modifier.height(8.dp))
        SocialButton({}, R.drawable.google, "Continuar con Google")
        Spacer(modifier = Modifier.height(8.dp))
        SocialButton({}, R.drawable.facebook, "Continuar con Facebook")
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun LoginField(value: String) {
    TextField(
        value = value,
        onValueChange = {},
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.fillMaxWidth())
}

@Composable
fun SocialButton(onClick: () -> Unit, drawableId: Int, value: String) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Color.White),
        modifier = Modifier.fillMaxWidth().border(2.dp, Color.LightGray, RoundedCornerShape(5.dp))
    ) {
        Image(
            painter = painterResource(drawableId),
            contentDescription = null,
            modifier = Modifier.padding(end = 5.dp).size(16.dp)
        )
        Text(value, color = Color.Black)
    }
}