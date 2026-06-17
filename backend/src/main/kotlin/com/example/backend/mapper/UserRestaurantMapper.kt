package com.example.backend.mapper

import com.example.backend.model.UserRestaurant
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserRestaurantMapper {
    fun findAll(): List<UserRestaurant>?
    fun findByIdUserId(userId: Int?): List<UserRestaurant>?
    fun findByIdUserIdAndIdRestaurantId(@Param("userId") userId: Int,@Param("restaurantId") restaurantId: Int): UserRestaurant?
    fun upsert(userRestaurant: UserRestaurant)
    fun deleteByIdUserIdAndIdRestaurantId(userId: Int, restaurantId: Int)
}