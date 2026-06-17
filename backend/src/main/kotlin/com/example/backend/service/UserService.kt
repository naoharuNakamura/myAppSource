package com.example.backend.service

import com.example.backend.mapper.UserMapper
import com.example.backend.config.AppProperties
import com.example.backend.dto.auth.LoginRequest
import com.example.backend.dto.user.UserRegisterRequest
import com.example.backend.dto.user.UserResponse
import com.example.backend.dto.user.UserUpdateRequest
import com.example.backend.model.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userMapper: UserMapper,
    private val appProperties: AppProperties
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun registerUser(user: User) {
        user.userPassword = passwordEncoder.encode(user.userPassword) ?: ""
        userMapper.insertUser(user)
    }

    fun verifyUser(user: LoginRequest): Boolean {
        val storedUser = userMapper.verifyUserByUserEmail(user.userEmail)
        return storedUser != null && passwordEncoder.matches(user.userPassword, storedUser.userPassword)
    }

    fun existsByUserEmail(userEmail: String): Boolean {
        return userMapper.existsByUserEmail(userEmail)
    }

    fun getUserByUserEmail(userEmail: String): UserResponse? {
        val userEntity = userMapper.findUserByUserEmail(userEmail) ?: return null

        // 🌟ここでDTOに詰め替える！
        return UserResponse(
            userId = userEntity.userId,
            userName = userEntity.userName,
            userEmail = userEntity.userEmail,
            userPhone = userEntity.userPhone
        )
    }

    fun updateUser(updateUserData: UserUpdateRequest): User {


        val exsistingUser = userMapper.findUserByUserId(updateUserData.userId)

        if (!updateUserData.userEmail.isNullOrEmpty()) {
            if (updateUserData.userEmail != exsistingUser.userEmail) {
                if (userMapper.existsByUserEmail(updateUserData.userEmail)) {
                    throw RuntimeException("This Email is not variable")
                }
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
        return exsistingUser
    }
}