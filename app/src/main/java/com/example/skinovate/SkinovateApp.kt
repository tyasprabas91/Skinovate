package com.example.skinovate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.skinovate.screen.SkinQuestionnaireScreen
import com.example.skinovate.screen.RoutineMakerScreen
import com.example.skinovate.screen.SkinovateHomeScreen
import com.example.skinovate.screen.ProfileScreen
import com.example.skinovate.screen.PersonalInformationScreen
import com.example.skinovate.screen.HistoryAnalysisScreen
import com.example.skinovate.screen.RoutineRecommendationScreen
import com.example.skinovate.screen.LearningScreen
import com.example.skinovate.screen.ProblemDetailScreen
import com.example.skinovate.screen.components.ChatBotDialog
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
    
    // Observe current user to reinitialize repositories when user logs in
    val currentUser by com.example.skinovate.auth.AuthRepository.currentUser.collectAsState()
    
    // Initialize repositories when user is logged in
    if (context != null && currentUser != null) {
        LaunchedEffect(currentUser?.id) {
            // Reinitialize repositories to load data from database for the logged-in user
            com.example.skinovate.data.UserRepository.init(context)
            com.example.skinovate.data.RoutineRepository.init(context)
            com.example.skinovate.data.ProductRepository.init(context)
        }
    }

    // List of screens for the bottom bar
    val items = listOf(Screen.Home, Screen.HistoryAnalysis, Screen.Learning, Screen.Products, Screen.Profile)

    var showChatBot by remember { mutableStateOf(false) }
    
    Scaffold(
        bottomBar = {
            SkinovateBottomBar(navController = navController, items = items)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showChatBot = true },
                modifier = Modifier.padding(bottom = 80.dp), // Above bottom bar
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.HeadsetMic,
                    contentDescription = "Chatbot",
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
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

            // Screen 2: History Analysis
            composable(Screen.HistoryAnalysis.route) {
                HistoryAnalysisScreen(navController = navController)
            }

            // Screen 3: Learning/Edukasi
            composable(Screen.Learning.route) {
                LearningScreen(navController = navController)
            }

            // Screen 4: Products
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

            // Screen 5: Routine Maker
            composable(Screen.RoutineMaker.route) {
                RoutineMakerScreen(navController)
            }

            // Screen 6: Routine Recommendation
            composable(Screen.RoutineRecommendation.route) {
                RoutineRecommendationScreen(navController = navController)
            }

            // Screen 7: Skin Questionnaire
            composable(Screen.SkinQuestionnaire.route) {
                SkinQuestionnaireScreen(navController)
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
            
            // Learning/Problem Detail
            composable("problem_detail/{problemId}") { backStackEntry ->
                val problemId = backStackEntry.arguments?.getString("problemId") ?: ""
                ProblemDetailScreen(
                    navController = navController,
                    problemId = problemId
                )
            }
        }
    }
    
    // ChatBot Dialog
    if (showChatBot) {
        ChatBotDialog(
            onDismiss = { showChatBot = false }
        )
    }
}

@Composable
fun SkinovateBottomBar(navController: NavController, items: List<Screen>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
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