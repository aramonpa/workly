package com.aramonp.workly.presentation.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aramonp.workly.R
import com.aramonp.workly.navigation.Route
import com.aramonp.workly.presentation.component.BottomNavigationBar
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = hiltViewModel()) {
    val notificationState = viewModel.notificationsState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    stringResource(R.string.profile_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(Route.SettingsScreen.route) },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    ) {
                    Image(imageVector = Icons.Default.Settings, contentDescription = stringResource(R.string.settings_title))
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                    ) {
                        Text(stringResource(R.string.settings_title), fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.settings_description))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                        )
                }
            }


            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Image(imageVector = Icons.Default.Notifications, contentDescription = stringResource(R.string.notifications_title))
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                ) {
                    Text(stringResource(R.string.notifications_title), fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.notifications_description))
                }
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = notificationState.value,
                    onCheckedChange = {
                        coroutineScope.launch {
                            viewModel.saveNotificationState(it)
                        }

                    }
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Spacer(modifier = Modifier.weight(1f))
            Button(
                shape = RoundedCornerShape(5.dp),
                onClick = {
                    coroutineScope.launch {
                        viewModel.signOut()
                        navController.navigate(Route.LogInScreen.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color.Red),
            ) {
                Text(stringResource(R.string.close_session_button_text))
            }
        }
    }
}
