package com.example.backend.dto.user

import com.example.backend.model.User

data class UserResponse(
    val userId: Int?,
    val userName: String,
    val userEmail: String,
    val userPhone: String
) {
    constructor(user: User) : this(
        userId = user.userId,
        userName = user.userName,
        userEmail = user.userEmail,
        userPhone = user.userPhone
    )
}