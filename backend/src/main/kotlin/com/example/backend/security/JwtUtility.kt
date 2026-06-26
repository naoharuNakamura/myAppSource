package com.example.backend.security

import com.example.backend.properties.JwtProperties
import com.example.backend.exception.DomainException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.security.Key
import java.util.Date
import java.nio.charset.StandardCharsets

@Component
class JwtUtility(
    private val jwtProperties: JwtProperties
) {
    private  val expirationTime: Long = jwtProperties.expiration
    
    private  val signingKey: Key by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secretKey.toByteArray(StandardCharsets.UTF_8))
    }


    fun generateToken(email: String): String {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getEmailFromToken(token: String): String {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            throw DomainException.Auth.Token("${e.message}")
            false
        }
    }
}