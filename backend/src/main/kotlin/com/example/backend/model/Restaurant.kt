package com.example.backend.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Restaurant(
    var restaurantId: Int = 0,
    var restaurantName: String = "",
    var restaurantImg: String = "",
    var restaurantRating: Double = 0.0, 
    var restaurantGenre: String = "",
    var restaurantPriceRange: String = "",
    var restaurantArea: String = "",
    var restaurantOpenHour: String = "",
    var restaurantCloseHour: String = "",
    var restaurantAddress: String = "",
    var restaurantPhone: String = "",
    var restaurantUrl: String = "",
    var restaurantClosedDays: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null,
    var isFavorite: Boolean = false,
    
    @JsonProperty("is_geocoded")
    var isGeocoded: Boolean = false
)
