package com.example.backend.dto.auth

import com.example.backend.constants.AppConstants
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

data class LoginRequest(
    
    @Email
    val userEmail: String,
        
    @Pattern(regexp = AppConstants.PASSWORD_REGEX)
    val userPassword: String
)