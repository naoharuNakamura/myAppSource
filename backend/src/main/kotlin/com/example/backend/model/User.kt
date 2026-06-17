package com.example.backend.model

data class User(
    var userId: Int = 0,
    var userPassword: String? = "",
    var userName: String = "",
    var userEmail: String = "",
    var userPhone: String = ""
)