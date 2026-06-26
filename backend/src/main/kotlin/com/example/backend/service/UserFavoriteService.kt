package com.example.backend.service

import com.example.backend.dto.userfavorite.UserFavoriteResponse
import com.example.backend.exception.DomainException
import com.example.backend.mapper.RestaurantMapper
import com.example.backend.mapper.UserFavoriteMapper
import com.example.backend.model.Restaurant
import com.example.backend.model.UserFavorite
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class UserFavoriteService(
    val userFavoriteMapper: UserFavoriteMapper,
    val restaurantMapper: RestaurantMapper
) {
    fun getAllUserFavorites(): List<UserFavorite> = userFavoriteMapper.findAll() ?: emptyList()

    @Transactional
    fun toggleFavorite(userId: Int, restaurantId: Int): UserFavoriteResponse {
        val restaurant = restaurantMapper.findByRestaurantId(restaurantId) ?: throw DomainException.ResourceNotFound()
        val existing = userFavoriteMapper.findByIdUserIdAndIdRestaurantId(userId, restaurantId)

        if (existing == null) {

            val newFavorite = UserFavorite(
                userFavoriteId = UserFavorite.UserFavoriteId(userId, restaurantId),
                isFavorite = true
            )
            userFavoriteMapper.upsert(newFavorite)
            return UserFavoriteResponse("added")
        }
        existing.isFavorite = !existing.isFavorite

        val shouldDelete = !existing.isFavorite

        if (shouldDelete) {
            userFavoriteMapper.deleteByIdUserIdAndIdRestaurantId(userId, restaurantId)
            return UserFavoriteResponse("removed")
        }

        userFavoriteMapper.upsert(existing)
        return UserFavoriteResponse(if (existing.isFavorite) "added" else "removed")
    }

    fun getUserFavorites(userId: Int?): List<Int> {
        if (userId == null) throw DomainException.Unauthorized()
        val favorites = userFavoriteMapper.findByIdUserId(userId) ?: emptyList()
        val resultIds = favorites.filter { it.isFavorite }.map { it.userFavoriteId.restaurantId }
        return resultIds
    }

    fun getFavoriteDetails(userId: Int?): List<Restaurant> {
        if (userId == null) throw DomainException.Unauthorized()
        val favorites = (userFavoriteMapper.findByIdUserId(userId) ?: emptyList())
        val ids = favorites.filter { it.isFavorite }.map { it.userFavoriteId.restaurantId }
        if (ids.isEmpty()) return emptyList()
        val restaurants = restaurantMapper.findAllByIds(ids)
        restaurants.forEach { it.isFavorite = true }
        return restaurants
    }

    fun getUserFavorite(userId: Int, restaurantId: Int): UserFavorite? {
        return userFavoriteMapper.findByIdUserIdAndIdRestaurantId(userId, restaurantId)
    }
}