package com.example.backend.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class UserRestaurant(
    var userRestaurantId: UserRestaurantId = UserRestaurantId(0,0),

    @JsonProperty("isFavorite")
    var isFavorite: Boolean = false,

    var userMemo: String? = null
) : Serializable {
    data class UserRestaurantId(
        var userId: Int = 0,
        var restaurantId: Int = 0
    ) : Serializable
}
