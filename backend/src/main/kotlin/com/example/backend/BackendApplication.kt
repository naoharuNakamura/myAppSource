package com.example.backend

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootApplication
@ConfigurationPropertiesScan
class BackendApplication {
    @Bean
    fun runBackend(jdbcTemplate: JdbcTemplate) = CommandLineRunner {
        println("=== Run ===")
    }
}

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}


