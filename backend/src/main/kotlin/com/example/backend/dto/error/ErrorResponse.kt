package com.example.backend.dto.error

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ErrorResponse(
    val status: Int,
    val errorCode: String,
    val message: String,
    val details: Map<String, String>? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)