package com.example.backend.service

import com.example.backend.dto.userrestaurant.UserRestaurantFavoriteResponse
import com.example.backend.mapper.RestaurantMapper
import com.example.backend.mapper.UserRestaurantMapper
import com.example.backend.model.Restaurant
import com.example.backend.model.UserRestaurant
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import javax.sql.DataSource

@Service
class UserRestaurantService(
    val userRestaurantMapper: UserRestaurantMapper,
    val restaurantMapper: RestaurantMapper,
    val dataSource: DataSource
) {
    fun getAllUserRestaurants(): List<UserRestaurant> = userRestaurantMapper.findAll() ?: emptyList()

    @Transactional
    fun toggleFavorite(userId: Int, restaurantId: Int): UserRestaurantFavoriteResponse {
        val existing = userRestaurantMapper.findByIdUserIdAndIdRestaurantId(userId, restaurantId) 


        if (existing == null) {
         
            val newFavorite = UserRestaurant(
                userRestaurantId = UserRestaurant.UserRestaurantId(userId, restaurantId),
                isFavorite = true
            )
            userRestaurantMapper.upsert(newFavorite)
            return UserRestaurantFavoriteResponse("added")
        }
        existing.isFavorite = !existing.isFavorite 

        val shouldDelete = !existing.isFavorite && existing.userMemo.isNullOrBlank()

        if (shouldDelete) {
            userRestaurantMapper.deleteByIdUserIdAndIdRestaurantId(userId, restaurantId)
            return UserRestaurantFavoriteResponse("removed")
        }

        userRestaurantMapper.upsert(existing)
        return UserRestaurantFavoriteResponse(if (existing.isFavorite) "added" else "removed")
    }

    fun getMemoRestaurant(userId: Int, restaurantId: Int): UserRestaurant? {
        val result = userRestaurantMapper.findByIdUserIdAndIdRestaurantId(userId, restaurantId)
        return result
    }

    fun editMemoRestaurant(userId: Int, restaurantId: Int, memo: String?): UserRestaurant {
        
        val userRestaurant = userRestaurantMapper.findByIdUserIdAndIdRestaurantId(userId, restaurantId)
            ?: UserRestaurant(
                userRestaurantId = UserRestaurant.UserRestaurantId(userId, restaurantId),
                isFavorite = false
            )

        userRestaurant.userMemo = memo

        userRestaurantMapper.upsert(userRestaurant)
  
        return userRestaurant
    }

    fun getUserFavorites(userId: Int?): List<Int> {
        val dbFile = File("database.db")
        // null 混入の心配がなくなったので、単純に取得してフィルタリングするだけです
        val favorites = userRestaurantMapper.findByIdUserId(userId) ?: emptyList()

        // 警告が出ていたループや複雑なチェックは不要になりました
        val resultIds = favorites
            .filter { it.isFavorite }
            .map { it.userRestaurantId.restaurantId }
        return resultIds
    }

    fun getFavoriteDetails(userId: Int?): List<Restaurant> {
        val connection = dataSource.connection
        val statement = connection.createStatement()
        val rs = statement.executeQuery("SELECT * FROM t_user_restaurant WHERE user_id = '1'")

        val results = mutableListOf<String>()
        while (rs.next()) {
            results.add(rs.getString("restaurant_id"))
        }
        rs.close()
        statement.close()
        connection.close()
        val favorites = (userRestaurantMapper.findByIdUserId(userId) ?: emptyList())
        val ids = favorites
            .filter { it.isFavorite }
            .map { it.userRestaurantId.restaurantId }
        if (ids.isEmpty()) return emptyList()

        val restaurants = restaurantMapper.findAllByIds(ids)
        restaurants.forEach { it.isFavorite = true }
        return restaurants
    }

    fun getUserRestaurant(userId: Int, restaurantId: Int): UserRestaurant? {
        return userRestaurantMapper.findByIdUserIdAndIdRestaurantId(userId, restaurantId)
    }
}