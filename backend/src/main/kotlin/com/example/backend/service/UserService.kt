package com.example.backend.service

import com.example.backend.mapper.UserMapper
import com.example.backend.properties.AppProperties
import com.example.backend.dto.auth.LoginRequest
import com.example.backend.dto.user.UserRegisterRequest
import com.example.backend.dto.user.UserResponse
import com.example.backend.dto.user.UserUpdateRequest
import com.example.backend.exception.DomainException
import com.example.backend.model.User
import com.example.backend.security.JwtUtility
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userMapper: UserMapper,
    private val appProperties: AppProperties,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtility: JwtUtility
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun existsByUserEmail(userEmail: String) {if(userMapper.existsByUserEmail(userEmail))  throw DomainException.User.DuplicateEmail()}
    
    @Transactional
    fun registerUser(requestUser: UserRegisterRequest) {
        existsByUserEmail(requestUser.userEmail)
        requestUser.userPassword = passwordEncoder.encode(requestUser.userPassword) ?: throw DomainException.Unknown("")
        val user = User(
            userPassword = requestUser.userPassword,
            userName = requestUser.userName,
            userEmail = requestUser.userEmail,
            userPhone = requestUser.userPhone
        )
        userMapper.insertUser(user)
    }

    fun verifyUser(user: LoginRequest): Boolean {
        val storedUser = userMapper.verifyUserByUserEmail(user.userEmail)
        return storedUser != null && passwordEncoder.matches(user.userPassword, storedUser.userPassword)
    }

    fun getUserByUserEmail(userEmail: String): UserResponse? {
        val userEntity = userMapper.findUserByUserEmail(userEmail) ?: return null

        return UserResponse(
            userId = userEntity.userId,
            userName = userEntity.userName,
            userEmail = userEntity.userEmail,
            userPhone = userEntity.userPhone
        )
    }

    @Transactional
    fun updateUser(updateUserData: UserUpdateRequest): UserResponse {


        val exsistingUser = userMapper.findUserByUserId(updateUserData.userId) ?: throw DomainException.ResourceNotFound()

        if (!updateUserData.userEmail.isNullOrEmpty()) {
            if (updateUserData.userEmail != exsistingUser.userEmail) {
                existsByUserEmail(updateUserData.userEmail)
            }
            exsistingUser.userEmail = updateUserData.userEmail
        }

        if (!updateUserData.userName.isNullOrEmpty()) {
            exsistingUser.userName = updateUserData.userName
        }

        if (!updateUserData.userPhone.isNullOrEmpty()) {
            exsistingUser.userPhone = updateUserData.userPhone
        }

        if (!updateUserData.userPassword.isNullOrEmpty()) {
            exsistingUser.userPassword = passwordEncoder.encode(updateUserData.userPassword)
        }

        userMapper.updateUser(exsistingUser)
        return UserResponse(exsistingUser)
    }
    
    fun getUserName(userId: Int): String? = userMapper.getUserNameByUserId(userId)
}