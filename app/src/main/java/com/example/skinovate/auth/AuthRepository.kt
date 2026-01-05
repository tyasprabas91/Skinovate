package com.example.skinovate.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthRepository {
    private const val PREFS_NAME = "skinovate_auth_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PHOTO_URL = "user_photo_url"
    private const val KEY_LOGIN_TYPE = "login_type" // "google" or "manual"
    
    // For manual login storage (simple in-memory for demo, should use secure storage in production)
    private val manualUsers = mutableMapOf<String, ManualUser>()

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Load initial state
        _isLoggedIn.value = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (_isLoggedIn.value) {
            _currentUser.value = User(
                id = prefs.getString(KEY_USER_ID, "") ?: "",
                name = prefs.getString(KEY_USER_NAME, "") ?: "",
                email = prefs.getString(KEY_USER_EMAIL, "") ?: "",
                photoUrl = prefs.getString(KEY_USER_PHOTO_URL, null)
            )
        }
    }

    // Auth State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun login(user: User, loginType: String = "google") {
        _isLoggedIn.value = true
        _currentUser.value = user

        // Persist to SharedPreferences
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_PHOTO_URL, user.photoUrl)
            putString(KEY_LOGIN_TYPE, loginType)
            apply()
        }
    }
    
    fun registerManualUser(email: String, password: String, name: String): Boolean {
        if (manualUsers.containsKey(email)) {
            return false // User already exists
        }
        manualUsers[email] = ManualUser(
            email = email,
            password = password, // In production, this should be hashed
            name = name
        )
        return true
    }
    
    fun loginManual(email: String, password: String): User? {
        val user = manualUsers[email]
        return if (user != null && user.password == password) {
            User(
                id = email,
                name = user.name,
                email = email,
                photoUrl = null
            )
        } else {
            null
        }
    }

    fun logout(context: Context) {
        _isLoggedIn.value = false
        _currentUser.value = null

        // Clear SharedPreferences
        prefs.edit().clear().apply()
        
        // Clear repository data
        com.example.skinovate.data.UserRepository.clearUserData()
        com.example.skinovate.data.RoutineRepository.clearUserData()
    }
    
    // Update user information
    fun updateUser(name: String, email: String, newPassword: String? = null): Boolean {
        val currentUser = _currentUser.value ?: return false
        val loginType = prefs.getString(KEY_LOGIN_TYPE, "") ?: ""
        
        // Update manual user if exists
        if (loginType == "manual" && manualUsers.containsKey(currentUser.email)) {
            val manualUser = manualUsers[currentUser.email]!!
            val updatedUser = manualUser.copy(
                name = name,
                email = email,
                password = newPassword ?: manualUser.password
            )
            // If email changed, remove old entry and add new one
            if (email != currentUser.email) {
                manualUsers.remove(currentUser.email)
                manualUsers[email] = updatedUser
            } else {
                manualUsers[email] = updatedUser
            }
        }
        
        // Update current user
        val updatedUser = User(
            id = if (email != currentUser.email) email else currentUser.id,
            name = name,
            email = email,
            photoUrl = currentUser.photoUrl
        )
        
        _currentUser.value = updatedUser
        
        // Persist to SharedPreferences
        prefs.edit().apply {
            putString(KEY_USER_ID, updatedUser.id)
            putString(KEY_USER_NAME, updatedUser.name)
            putString(KEY_USER_EMAIL, updatedUser.email)
            apply()
        }
        
        return true
    }
    
    // Get current login type
    fun getLoginType(): String {
        return prefs.getString(KEY_LOGIN_TYPE, "") ?: ""
    }
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null
)

data class ManualUser(
    val email: String,
    val password: String,
    val name: String
)

