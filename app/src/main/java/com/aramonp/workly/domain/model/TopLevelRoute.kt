package com.aramonp.workly.domain.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.aramonp.workly.navigation.Route

data class TopLevelRoute (
    val name: String,
    val route: Route,
    val icon: ImageVector
)