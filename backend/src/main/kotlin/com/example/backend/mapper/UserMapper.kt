package com.example.backend.mapper

import com.example.backend.dto.user.UserRegisterRequest
import com.example.backend.dto.user.UserResponse
import com.example.backend.model.User
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UserMapper {
    fun verifyUserByUserEmail(userEmail: String): User?
    fun findUserByUserId(userId: Int): User?
    fun findUserByUserEmail(userEmail: String): User?
    fun getUserNameByUserId(userId: Int): String? 
    fun existsByUserEmail(userEmail: String): Boolean
    fun insertUser(user: User)
    fun updateUser(user: User)
}