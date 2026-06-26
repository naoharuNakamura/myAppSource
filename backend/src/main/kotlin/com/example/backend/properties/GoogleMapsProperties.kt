package com.example.backend.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "google.maps")
data class GoogleMapsProperties(
    val apiKey: String? = ""
)
