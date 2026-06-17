package com.example.backend.security

import com.example.backend.mapper.UserMapper
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userMapper: UserMapper
): UserDetailsService {

    override fun loadUserByUsername(userEmail: String): UserDetails {
        val user = userMapper.verifyUserByUserEmail(userEmail)
            ?: throw UsernameNotFoundException("UserNotFoundByThis$userEmail")
        return UserPrincipal(user)
    }
}