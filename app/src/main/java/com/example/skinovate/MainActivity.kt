package com.example.skinovate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skinovate.auth.AuthRepository
import com.example.skinovate.auth.AuthViewModel
import com.example.skinovate.auth.AuthUiState
import com.example.skinovate.data.ProductRepository
import com.example.skinovate.data.RoutineRepository
import com.example.skinovate.data.UserRepository
import com.example.skinovate.data.database.DatabaseModule
import com.example.skinovate.screen.AuthScreen
import com.example.skinovate.ui.theme.SkinovateTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Initialize AuthRepository
            AuthRepository.init(this)
            
            // Initialize Database (singleton pattern - will be created on first access)
            // This is non-blocking as Room uses background thread internally
            DatabaseModule.getDatabase(this)
            
            // Initialize Repositories (all use coroutines, non-blocking)
            UserRepository.init(this)
            RoutineRepository.init(this)
            ProductRepository.init(this)
            
            // Setup Notification Channels (with error handling)
            try {
                com.example.skinovate.notifications.NotificationHelper.createNotificationChannels(this)
                com.example.skinovate.utils.RoutineTimerHelper.createTimerChannel(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Initialize Notification Settings (with error handling) - MUST be called before accessing StateFlow
            try {
                com.example.skinovate.notifications.NotificationSettingsRepository.init(this)
                
                // Schedule notifications if enabled (only after init)
                try {
                    if (com.example.skinovate.notifications.NotificationSettingsRepository.routineRemindersEnabled.value) {
                        com.example.skinovate.notifications.RoutineNotificationManager.scheduleRoutineNotifications(this)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Continue even if notification setup fails
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Log error but continue - app should still launch
        }

        setContent {
            SkinovateTheme {
                AuthContent()
            }
        }
    }

    @Composable
    fun AuthContent() {
        val isLoggedIn by AuthRepository.isLoggedIn.collectAsStateWithLifecycle()
        val context = this@MainActivity

        if (isLoggedIn) {
            // User is logged in, show main app
            SkinovateApp(
                authViewModel = authViewModel,
                context = context,
                onLogout = {
                    // Logout will trigger recomposition and show auth screen
                }
            )
        } else {
            // User is not logged in, show auth screen
            AuthScreen(
                viewModel = authViewModel,
                context = context,
                onSignInSuccess = {
                    // Navigation handled by LaunchedEffect in AuthScreen
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AuthViewModel.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            authViewModel.handleSignInResult(task)
        }
    }
}