package com.example.backend.model

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
    var isFavorite: Boolean = false
)
