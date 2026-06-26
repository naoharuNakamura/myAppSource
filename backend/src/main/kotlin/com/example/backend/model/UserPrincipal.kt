package com.example.backend.model

import com.example.backend.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserPrincipal(
    val user: User
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String? {
        return user.userPassword
    }

    override fun getUsername(): String {
        return user.userEmail
    }

    // アカウントの有効期限が切れていないか
    override fun isAccountNonExpired(): Boolean = true

    // アカウントがロックされていないか
    override fun isAccountNonLocked(): Boolean = true

    // 認証情報の有効期限が切れていないか
    override fun isCredentialsNonExpired(): Boolean = true

    // アカウントが有効か
    override fun isEnabled(): Boolean = true

    val userId: Int get() = user.userId
} 
