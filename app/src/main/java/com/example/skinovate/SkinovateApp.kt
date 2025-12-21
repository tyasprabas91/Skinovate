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
import com.example.skinovate.screens.SkinovateHomeScreen

@Composable
fun SkinovateApp() {
    val navController = rememberNavController()

    // List of screens for the bottom bar
    val items = listOf(Screen.Home, Screen.Features, Screen.Products, Screen.Settings)

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
                SkinovateHomeScreen()
            }

            // Screen 2: Features (Placeholder)
            composable(Screen.Features.route) {
                Text("Features Screen Placeholder")
            }

            // Screen 3: Products (Placeholder)
            composable(Screen.Products.route) {
                Text("Products Screen Placeholder")
            }

            // Screen 4: Settings (Placeholder)
            composable(Screen.Settings.route) {
                Text("Settings Screen Placeholder")
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
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
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