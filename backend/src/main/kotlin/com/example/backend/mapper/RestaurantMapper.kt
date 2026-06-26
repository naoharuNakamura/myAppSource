package com.example.backend.mapper

import com.example.backend.dto.restaurant.RestaurantSearchRequest
import com.example.backend.model.Restaurant
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface RestaurantMapper {
    fun findDistinctGenres(): List<String>
    fun findDistinctPriceRanges(): List<String>
    fun findDistinctAreas(): List<String>
    fun findDistinctRatings(): List<Double>
    fun findAll(): List<Restaurant>
    fun findAllByIds(@Param("restaurantId") restaurantId: List<Int>): List<Restaurant>
    fun findByRestaurantId(restaurantId: Int): Restaurant?
    
    fun searchRestaurants(request: RestaurantSearchRequest): List<Restaurant>?
    
    fun findByRestaurantIdForUpdate(restaurantId: Int): Restaurant?    
    fun updateRestaurant(restaurant: Restaurant)
    
    fun checkRestaurantExists(restaurantId: Int): Boolean
}