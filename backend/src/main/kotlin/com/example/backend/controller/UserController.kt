package com.example.backend.controller

import com.example.backend.constants.ApiEndpoints
import com.example.backend.dto.auth.LoginRequest
import com.example.backend.dto.auth.LoginResponse
import com.example.backend.dto.user.UserRegisterRequest
import com.example.backend.dto.user.UserResponse
import com.example.backend.dto.user.UserUpdateRequest
import com.example.backend.model.User
import com.example.backend.service.AuthService
import com.example.backend.service.UserService

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
    private val authService: AuthService
) {

    @PostMapping(ApiEndpoints.User.REGISTER)
    fun register(@Valid @RequestBody request: UserRegisterRequest): ResponseEntity<*> =
        ResponseEntity.ok(userService.registerUser(request))


    @PostMapping(ApiEndpoints.User.LOGIN)
    fun login(@RequestBody @Valid loginRequest: LoginRequest): ResponseEntity<LoginResponse> =
        ResponseEntity.ok(authService.login(loginRequest))

    @PutMapping(ApiEndpoints.User.PROFILE)
    fun updateProfile(@RequestBody updateUserData: UserUpdateRequest): ResponseEntity<*> =
        ResponseEntity.ok(userService.updateUser(updateUserData))


    @GetMapping(ApiEndpoints.User.CHECK_EMAIL)
    fun checkEmailExists(@Valid @RequestParam("userEmail") userEmail: String): ResponseEntity<*> =
        ResponseEntity.ok(userService.existsByUserEmail(userEmail))

}