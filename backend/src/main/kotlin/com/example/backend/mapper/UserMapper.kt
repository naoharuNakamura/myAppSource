package com.example.backend.mapper

import com.example.backend.dto.user.UserRegisterRequest
import com.example.backend.dto.user.UserResponse
import com.example.backend.model.User
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UserMapper {
    fun verifyUserByUserEmail(useremail: String): User?
    fun findUserByUserId(userid: Int): User
    fun findUserByUserEmail(userEmail: String): User?
    fun existsByUserEmail(userEmail: String): Boolean
    fun insertUser(user: User)
    fun updateUser(user: User)
}