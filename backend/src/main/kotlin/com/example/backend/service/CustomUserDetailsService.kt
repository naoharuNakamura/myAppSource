package com.example.backend.service

import com.example.backend.exception.DomainException
import com.example.backend.mapper.UserMapper
import com.example.backend.model.UserPrincipal
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
            ?: throw DomainException.ResourceNotFound()
        return UserPrincipal(user)
    }
}