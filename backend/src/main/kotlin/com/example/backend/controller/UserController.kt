package com.example.backend.controller

import com.example.backend.service.UserService
import com.example.backend.constants.ApiEndpoints
import com.example.backend.dto.auth.LoginRequest
import com.example.backend.dto.auth.LoginResponse
import com.example.backend.dto.user.UserRegisterRequest
import com.example.backend.dto.user.UserResponse
import com.example.backend.dto.user.UserUpdateRequest
import com.example.backend.model.User
import com.example.backend.security.JwtAuthFilter
import com.example.backend.security.JwtUtility

import jakarta.validation.Valid

import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException

@RestController
@RequestMapping(ApiEndpoints.User.ROOT)
class UserController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtility
    ) {

    @PostMapping(ApiEndpoints.User.REGISTER)
    fun register(@Valid @RequestBody request: UserRegisterRequest): ResponseEntity<*> {
        val user = User().apply { 
            userEmail = request.userEmail
            userPassword = request.userPassword
            userName = request.userName
            userPhone = request.userPhone
        }
        
        if (userService.existsByUserEmail(user.userEmail)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists")
        }
        
        return ResponseEntity.ok(userService.registerUser(user))
    }

    @PostMapping(ApiEndpoints.User.LOGIN)
    fun login(@RequestBody @Valid loginRequest: LoginRequest): ResponseEntity<*> {
        return try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.userEmail,
                    loginRequest.userPassword
                )
            )
            
            val user = userService.getUserByUserEmail(loginRequest.userEmail)
                ?: throw RuntimeException("no such user")

            val token = jwtUtil.generateToken(user.userEmail)
            ResponseEntity.ok(LoginResponse(token, user))

        } catch (e: AuthenticationException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password")
        }
    }
    
    @PutMapping(ApiEndpoints.User.PROFILE)
    fun updateProfile(@RequestBody updateUserData: UserUpdateRequest): ResponseEntity<*> {
        return try {
            val updatedUser = userService.updateUser(updateUserData)
            ResponseEntity.ok(UserResponse(updatedUser))
        } catch (e: RuntimeException) {
            when{
                e.message?.contains("This Email is not variable") == true -> 
                ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
            else -> 
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
            }
        }
    }
    
    @GetMapping(ApiEndpoints.User.CHECK_EMAIL)
    fun checkEmailExists(@Valid @RequestParam("userEmail") userEmail: String): ResponseEntity<Boolean> =
 ResponseEntity.ok(userService.existsByUserEmail(userEmail))

}