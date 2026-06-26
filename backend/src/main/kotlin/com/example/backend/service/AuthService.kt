package com.example.backend.service

import com.example.backend.dto.auth.LoginRequest
import com.example.backend.dto.auth.LoginResponse
import com.example.backend.dto.user.UserResponse
import com.example.backend.exception.DomainException
import com.example.backend.security.JwtUtility
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtility,
    private val userService: UserService
) {
fun login(loginRequest: LoginRequest): LoginResponse {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.userEmail,
                        loginRequest.userPassword
                )
            )
        }catch (e: AuthenticationException){
                throw DomainException.Unauthorized()
        }
        
        val user = userService.getUserByUserEmail(loginRequest.userEmail) ?: throw DomainException.ResourceNotFound()
        val token = jwtUtil.generateToken(user.userEmail)
        return LoginResponse(token, user)
    }
}