package com.example.backend.security

import com.example.backend.mapper.UserMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtUtil: JwtUtility,
    private val userMapper: UserMapper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        // 2. 「Bearer 」で始まるかチェック
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            val email = jwtUtil.getEmailFromToken(token)
            val user = userMapper.findUserByUserEmail(email)
            // 3. トークン検証と認証設定
            if (jwtUtil.validateToken(token)&& user!= null) {
                val userPrincipal = UserPrincipal(user)
                val authentication = UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null,
                    userPrincipal.authorities
                )
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        // 次のフィルターへ
        filterChain.doFilter(request, response)
    }
}