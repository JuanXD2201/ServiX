package com.example.presentation.viewmodel

import com.example.domain.model.AuthUser

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Authenticated(val user: AuthUser) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
