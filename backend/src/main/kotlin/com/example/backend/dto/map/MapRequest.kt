package com.example.backend.dto.map

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class MapRequest(
    @field:Min(-90) @field:Max(90) val lat: Double= 0.0,
    @field:Min(-180) @field:Max(180) val lng: Double = 0.0
)