package com.example.skinovate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.skinovate.navigation.Screen
import com.example.skinovate.screen.ProductScreen
import com.example.skinovate.screen.FaceAnalysisScreen
import com.example.skinovate.screen.RoutineMakerScreen
import com.example.skinovate.screen.SkinovateHomeScreen
import com.example.skinovate.screen.ProfileScreen
import com.example.skinovate.screen.PersonalInformationScreen
import com.example.skinovate.screen.FeaturesScreen
import com.example.skinovate.screen.NotificationSettingsScreen
import com.example.skinovate.screen.PrivacySettingsScreen
import com.example.skinovate.screen.AboutScreen
import com.example.skinovate.screen.HelpSupportScreen
import com.example.skinovate.screen.FAQScreen
import com.example.skinovate.auth.AuthViewModel
import android.content.Context

@Composable
fun SkinovateApp(
    authViewModel: AuthViewModel? = null,
    context: Context? = null,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()

    // List of screens for the bottom bar (excluding FaceAnalysis which is in the center)
    val items = listOf(Screen.Home, Screen.Features, Screen.Products, Screen.Profile)

    Scaffold(
        bottomBar = {
            SkinovateBottomBar(navController = navController, items = items)
        }
    ) { innerPadding ->
        // The NavHost swaps the screens
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Screen 1: Home
            composable(Screen.Home.route) {
                SkinovateHomeScreen(navController = navController)
            }

            // Screen 2: Features
            composable(Screen.Features.route) {
                FeaturesScreen(navController = navController)
            }

            // Screen 3: Products (Placeholder)
            composable(Screen.Products.route) {
                ProductScreen(navController)
            }

            // Screen 4: Profile
            composable(Screen.Profile.route) {
                if (authViewModel != null && context != null) {
                    ProfileScreen(
                        navController = navController,
                        authViewModel = authViewModel,
                        context = context,
                        onLogout = onLogout
                    )
                } else {
                    Text("Profile Screen Placeholder")
                }
            }

            // Screen 5: Routine Maker (Placeholder)
            composable(Screen.RoutineMaker.route) {
                RoutineMakerScreen(navController)
            }

            // Screen 6: Face Analysis
            composable(Screen.FaceAnalysis.route) {
                FaceAnalysisScreen(navController)
            }

            // Profile Sub-screens
            composable(Screen.PersonalInformation.route) {
                PersonalInformationScreen(navController = navController)
            }

            composable(Screen.NotificationSettings.route) {
                NotificationSettingsScreen(navController = navController)
            }

            composable(Screen.PrivacySettings.route) {
                PrivacySettingsScreen(navController = navController)
            }

            composable(Screen.About.route) {
                AboutScreen(navController = navController)
            }

            composable(Screen.HelpSupport.route) {
                HelpSupportScreen(navController = navController)
            }
            
            composable(Screen.FAQ.route) {
                FAQScreen(navController = navController)
            }
        }
    }
}

@Composable
fun SkinovateBottomBar(navController: NavController, items: List<Screen>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Split items into left and right for the center button
    val leftItems = items.take(2) // Home, Features
    val rightItems = items.drop(2) // Products, Profile

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left items
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    leftItems.forEach { screen ->
                        BottomNavItem(
                            screen = screen,
                            isSelected = currentRoute == screen.route,
                            onClick = {
                                handleNavigation(navController, screen)
                            }
                        )
                    }
                }

                // Center button - Face Analysis (larger and elevated like QRIS button)
                val isFaceAnalysisSelected = currentRoute == Screen.FaceAnalysis.route
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .offset(y = (-12).dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            spotColor = Color.Black.copy(alpha = 0.3f)
                        )
                        .clip(CircleShape)
                        .background(Color(0xFFFFC107)) // Yellow/Amber color
                        .clickable {
                            navController.navigate(Screen.FaceAnalysis.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Screen.FaceAnalysis.icon,
                        contentDescription = Screen.FaceAnalysis.title,
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }

                // Right items
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rightItems.forEach { screen ->
                        BottomNavItem(
                            screen = screen,
                            isSelected = currentRoute == screen.route,
                            onClick = {
                                handleNavigation(navController, screen)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = screen.title,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) {
                Color(0xFFFFC107) // Yellow when selected
            } else {
                Color.White.copy(alpha = 0.6f)
            }
        )
        Text(
            text = screen.title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) {
                Color(0xFFFFC107) // Yellow when selected
            } else {
                Color.White.copy(alpha = 0.6f)
            }
        )
    }
}

fun handleNavigation(navController: NavController, screen: Screen) {
    if (screen == Screen.Home) {
        navController.navigate(Screen.Home.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
            }
            launchSingleTop = true
        }
    } else {
        navController.navigate(screen.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}