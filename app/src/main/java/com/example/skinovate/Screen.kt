package com.example.skinovate.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

// 3. Define your routes safely
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Features : Screen("features", "Features", Icons.Default.Face)
    object Products : Screen("products", "Products", Icons.Default.ShoppingCart)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}