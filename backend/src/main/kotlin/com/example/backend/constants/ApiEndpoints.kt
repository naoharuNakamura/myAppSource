package com.example.backend.constants

object ApiEndpoints {
    const val BASE = "/api" 

    object User {
        const val ROOT = "$BASE/users"

        const val REGISTER = "/signup"
        const val LOGIN = "/login"
        const val PROFILE = "/profile"
        const val CHECK_EMAIL = "/check-email" 
    }

    object Favorite { 
        const val ROOT = "$BASE/favorites"

        const val LIST = ""            
        const val DETAILS = "/details"     
        const val ITEM = "/{restaurantId}" 
    }
    
    object Review {
        const val ROOT = "$BASE/review"
        
        const val REVIEW = "/{restaurantId}" 
        const val DELETE = "/{reviewId}"
    }

    object Restaurant {
        const val ROOT = "$BASE/restaurants"

        const val GENRES = "/genres"
        const val PRICE_RANGES = "/price-ranges"
        const val RATINGS = "/ratings"
        const val AREAS = "/areas"
        const val ALL = "/all"     
        const val SEARCH = "/search"
        const val DETAIL = "/{restaurantId}"
    }
}