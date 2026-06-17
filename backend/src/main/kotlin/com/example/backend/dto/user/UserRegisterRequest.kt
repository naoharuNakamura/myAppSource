package com.example.backend.dto.user

import jakarta.validation.constraints.*

import com.example.backend.constants.AppConstants

data class UserRegisterRequest(
    @Email
    val userEmail: String,
        
    @Pattern(regexp = AppConstants.PASSWORD_REGEX)
    val userPassword: String,
    
    @NotBlank
    val userName: String,
        
    @Pattern(regexp = AppConstants.PHONE_REGEX)
    val userPhone: String
)