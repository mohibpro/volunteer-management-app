package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.VolunteerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: UserEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val isRegistered: Boolean = false,
    val passwordResetSent: Boolean = false
)

class AuthViewModel(
    private val repository: VolunteerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }

    fun signIn(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.signIn(email, pass)
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, currentUser = user, error = null) }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, error = err.message ?: "Sign in failed") }
            }
        }
    }

    fun register(fullName: String, email: String, pass: String, role: UserRole) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.registerUser(fullName, email, pass, role)
            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = user,
                        isRegistered = true,
                        successMessage = "Welcome to ServeSync, ${user.fullName}!"
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, error = err.message ?: "Registration failed") }
            }
        }
    }

    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    passwordResetSent = true,
                    successMessage = "Password reset instructions sent to $email"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null, passwordResetSent = false) }
    }

    fun quickSwitchRole(role: UserRole) {
        viewModelScope.launch {
            repository.quickSwitchRole(role)
        }
    }

    fun switchUser(userId: Long) {
        viewModelScope.launch {
            repository.switchUser(userId)
        }
    }
}
