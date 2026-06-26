package com.example.backend.config

import com.example.backend.properties.GoogleMapsProperties
import com.google.maps.GeoApiContext
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GoogleMapsConfig(private val properties: GoogleMapsProperties){ 
    @Bean
    fun geoApiCotext(): GeoApiContext = GeoApiContext.Builder().apiKey(properties.apiKey).build()
}