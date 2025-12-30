package com.example.skinovate

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.skinovate.auth.AuthViewModel
import android.content.Context

@Composable
fun SkinovateApp(
    authViewModel: AuthViewModel? = null,
    context: Context? = null,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()

    // List of screens for the bottom bar
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

            // Screen 2: Features (Placeholder)
            composable(Screen.Features.route) {
                Text("Features Screen Placeholder")
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

            // Screen 6: Face Analysis (Placeholder)
            composable(Screen.FaceAnalysis.route) {
                FaceAnalysisScreen(navController)
            }


        }
    }
}

@Composable
fun SkinovateBottomBar(navController: NavController, items: List<Screen>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    // Check if we are clicking the Home button
                    if (screen == Screen.Home) {
                        // Pop everything up to Home, ensuring Home is the only thing left
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    } else {
                        // Normal navigation for other tabs
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.tertiary,
                    indicatorColor = MaterialTheme.colorScheme.tertiary,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f)
                )
            )
        }
    }
}