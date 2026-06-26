package com.example.backend.mapper

import com.example.backend.model.UserFavorite
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserFavoriteMapper {
    fun findAll(): List<UserFavorite>?
    fun findByIdUserId(userId: Int?): List<UserFavorite>?
    fun findByIdUserIdAndIdRestaurantId(@Param("userId") userId: Int,@Param("restaurantId") restaurantId: Int): UserFavorite?
    fun upsert(userFavorite: UserFavorite)
    fun deleteByIdUserIdAndIdRestaurantId(userId: Int, restaurantId: Int)
}