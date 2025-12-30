package com.example.skinovate.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var googleSignInClient: GoogleSignInClient? = null

    fun initialize(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
        
        // Check if user is already signed in
        checkExistingSignIn(context)
    }

    private fun checkExistingSignIn(context: Context) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) {
            handleSignInResult(account)
        }
    }

    fun getSignInIntent(): android.content.Intent? {
        return googleSignInClient?.signInIntent
    }

    fun signInWithGoogle(context: Context) {
        _uiState.value = AuthUiState.Loading
        initialize(context)
        val signInIntent = googleSignInClient?.signInIntent
        // Return intent, will be handled by Activity
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    handleSignInResult(it)
                } ?: run {
                    _uiState.value = AuthUiState.Error("Sign in failed")
                }
            } catch (e: ApiException) {
                _uiState.value = AuthUiState.Error("Sign in failed: ${e.message}")
            }
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount) {
        val user = User(
            id = account.id ?: "",
            name = account.displayName ?: "User",
            email = account.email ?: "",
            photoUrl = account.photoUrl?.toString()
        )

        AuthRepository.login(user, "google")
        _uiState.value = AuthUiState.Success(user)
    }
    
    fun loginManual(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val user = AuthRepository.loginManual(email, password)
            if (user != null) {
                AuthRepository.login(user, "manual")
                _uiState.value = AuthUiState.Success(user)
            } else {
                _uiState.value = AuthUiState.Error("Email atau password salah")
            }
        }
    }
    
    fun registerManual(email: String, password: String, name: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            if (email.isBlank() || password.isBlank() || name.isBlank()) {
                _uiState.value = AuthUiState.Error("Semua field harus diisi")
                return@launch
            }
            if (password.length < 6) {
                _uiState.value = AuthUiState.Error("Password minimal 6 karakter")
                return@launch
            }
            val success = AuthRepository.registerManualUser(email, password, name)
            if (success) {
                // Auto login after registration
                val user = AuthRepository.loginManual(email, password)
                if (user != null) {
                    AuthRepository.login(user, "manual")
                    _uiState.value = AuthUiState.Success(user)
                }
            } else {
                _uiState.value = AuthUiState.Error("Email sudah terdaftar")
            }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            googleSignInClient?.signOut()?.addOnCompleteListener {
                AuthRepository.logout()
                _uiState.value = AuthUiState.Idle
            } ?: run {
                // If client is null, just logout from repository
                AuthRepository.logout()
                _uiState.value = AuthUiState.Idle
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

