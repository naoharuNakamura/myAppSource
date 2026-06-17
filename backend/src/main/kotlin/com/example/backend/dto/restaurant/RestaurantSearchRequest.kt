package com.example.backend.dto.restaurant

data class RestaurantSearchRequest(
    val restaurantName: String = "",
    val restaurantRating: String? = null,
    val restaurantGenre: String = "",
    val restaurantPriceRange: String = "",
    val restaurantArea: String = "",
    val isAndSearch: Boolean = false,
    val minRating: Double? = null,
    val maxRating: Double? = null,
    val sort: String = "restaurantId,asc"
)

fun String.toRatingRange(): Pair<Double?, Double?> {
    return runCatching {
        val (min, max) = this.split("~").map { it.toDouble() }
        Pair(min, max)
    }.getOrDefault(Pair(null, null))
}