package com.example.backend.dto.restaurant

import com.example.backend.model.Restaurant

data class RestaurantSearchResponse(
    val restaurantId: Int = 0,
    val restaurantName: String = "",
    val restaurantGenre: String = "",
    val restaurantArea: String = "",
    val restaurantImg: String = "",
    val restaurantRating: Double? = null,
    val restaurantPriceRange: String = "",
    val restaurantOpenHour: String = "",
    val restaurantCloseHour: String = "",
    val restaurantClosedDays: String = "",
    val restaurantPhone: String = "",
    val restaurantAddress: String = "",
    val restaurantUrl: String = ""
) 

fun Restaurant.toSearchResponse(): RestaurantSearchResponse {
    return RestaurantSearchResponse(
        restaurantId = this.restaurantId,
        restaurantName = this.restaurantName,
        restaurantGenre = this.restaurantGenre,
        restaurantArea = this.restaurantArea,
        restaurantImg = this.restaurantImg,
        restaurantRating = this.restaurantRating,
        restaurantPriceRange = this.restaurantPriceRange,
        restaurantOpenHour = this.restaurantOpenHour,
        restaurantCloseHour = this.restaurantCloseHour,
        restaurantClosedDays = this.restaurantClosedDays,
        restaurantPhone = this.restaurantPhone,
        restaurantAddress = this.restaurantAddress,
        restaurantUrl = this.restaurantUrl
    )
}