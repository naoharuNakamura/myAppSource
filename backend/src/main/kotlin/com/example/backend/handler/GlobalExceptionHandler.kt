package com.example.backend.handler

import com.example.backend.dto.error.ErrorResponse
import com.example.backend.exception.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(DomainException::class)
    fun handleDomainException(e: DomainException): ResponseEntity<ErrorResponse>{

        val responsBody = ErrorResponse(
            status = e.status.value(),
            errorCode = e.errorCode,
            message = e.message ?: "UnknownError",
        )
        
        return ResponseEntity.status(e.status).body(responsBody)
    }
}