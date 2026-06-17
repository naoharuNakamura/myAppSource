package com.example.backend.config // ※ご自身のプロジェクトのパッケージ名に合わせてください


import com.example.backend.security.JwtAuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    // Autowiredの代わりにコンストラクタインジェクションを使用（Kotlinの推奨な書き方）
    private val jwtAuthenticationFilter: JwtAuthFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        println("====== [Debug] Security Configuration Loaded (Kotlin) ======")
        
        http
            // 🌟 CORS設定をここに集約 (OPTIONSも追加済み)
            .cors { cors ->
                cors.configurationSource {
                    val config = CorsConfiguration()
                    config.allowedOrigins = listOf("http://localhost:5173", "http://localhost:8080")
                    // OPTIONS を必ず含める！
                    config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    config.allowedHeaders = listOf("*")
                    config.allowCredentials = true
                    config
                }
            }
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/users/signup", "/api/users/login", "/api/users/check-email/**").permitAll()
                    .requestMatchers("/api/restaurants/**").permitAll()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().permitAll()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}