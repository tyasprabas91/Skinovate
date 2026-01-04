package com.example.skinovate.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

// 3. Define your routes safely
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Features : Screen("features", "Features", Icons.Default.Face)
    object Products : Screen("products", "Products", Icons.Default.ShoppingCart)
    object Profile : Screen("profile", "Profil", Icons.Default.Person)
    object RoutineMaker : Screen("routine_maker", "Routine", Icons.Default.List)
    object FaceAnalysis : Screen("face_analysis", "Face Scan", Icons.Default.Face)
    
    // Profile Sub-screens
    object PersonalInformation : Screen("personal_information", "Informasi Pribadi", Icons.Default.Person)
    object NotificationSettings : Screen("notification_settings", "Notifikasi", Icons.Default.Notifications)
    object PrivacySettings : Screen("privacy_settings", "Privasi", Icons.Default.Lock)
    object About : Screen("about", "Tentang", Icons.Default.Info)
    object HelpSupport : Screen("help_support", "Bantuan", Icons.Default.Info)
    object FAQ : Screen("faq", "FAQ", Icons.Default.Info)
}