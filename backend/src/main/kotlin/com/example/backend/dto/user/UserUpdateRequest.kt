package com.example.backend.dto.user

import com.example.backend.constants.AppConstants
import  jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern


data class UserUpdateRequest(
    val userId: Int = 0,
    
    @Email
    val userEmail: String? = null,
    
    @Pattern(regexp = AppConstants.PASSWORD_REGEX)
    val userPassword: String? = null,
        
    val userName: String? = null,
        
    @Pattern(regexp = AppConstants.PHONE_REGEX)
    val userPhone: String? = null
)