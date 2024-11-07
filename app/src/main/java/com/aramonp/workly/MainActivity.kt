package com.aramonp.workly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.produceState
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aramonp.workly.domain.repository.AuthRepository
import com.aramonp.workly.navigation.NavGraph
import com.aramonp.workly.navigation.Route
import com.aramonp.workly.ui.theme.WorklyTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.CustomInjection.inject
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navHostController = rememberNavController()
            WorklyTheme {
                NavGraph(navHostController, startDestination = getStartDestination())
            }
        }
    }

    private fun getStartDestination(): String {
        return if (firebaseAuth.currentUser != null) {
            Route.HomeScreen.route
        } else {
            Route.SignUpScreen.route
        }
    }
}
