package com.example.backend.constants

object AppConstants {
    const val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$"
    const val PHONE_REGEX = "^0\\d{1,4}-\\d{1,4}-\\d{4}$|^0\\d{9,10}$"
}

