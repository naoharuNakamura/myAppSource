package com.example.backend.dto.auth

import com.example.backend.dto.user.UserResponse

data class LoginResponse(
    val token: String,
    val user: UserResponse
)