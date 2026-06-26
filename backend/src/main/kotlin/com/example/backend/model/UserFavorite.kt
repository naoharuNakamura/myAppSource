package com.example.backend.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class UserFavorite(
    var userFavoriteId: UserFavoriteId = UserFavoriteId(0,0),

    @JsonProperty("isFavorite")
    var isFavorite: Boolean = false,
        
) : Serializable {
    data class UserFavoriteId(
        var userId: Int = 0,
        var restaurantId: Int = 0
    ) : Serializable
}
