package com.example.backend.controller

import com.example.backend.constants.ApiEndpoints
import com.example.backend.dto.userrestaurant.UserRestaurantFavoriteRequest
import com.example.backend.dto.userrestaurant.UserRestaurantMemoRequest
import com.example.backend.dto.userrestaurant.UserRestaurantFavoriteResponse
import com.example.backend.dto.userrestaurant.UserRestaurantMemoResponse
import com.example.backend.model.Restaurant
import com.example.backend.model.UserRestaurant
import com.example.backend.service.UserRestaurantService
import com.example.backend.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiEndpoints.Favorite.ROOT)
class UserRestaurantController(
    private val userRestaurantService: UserRestaurantService
) {

    @PostMapping(ApiEndpoints.Favorite.ITEM)
    fun toggleFavorite(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: UserRestaurantFavoriteRequest   // 型安全なDTOで受け取る
    ): ResponseEntity<UserRestaurantFavoriteResponse> {
        val userId = user.userId
        val response = userRestaurantService.toggleFavorite(userId, request.restaurantId)
        return ResponseEntity.ok(response)
    }

    @GetMapping(ApiEndpoints.Favorite.LIST)
    fun getUserFavorites(@AuthenticationPrincipal user: UserPrincipal): ResponseEntity<List<Int>> =
        ResponseEntity.ok(userRestaurantService.getUserFavorites(user.userId))
    
    @GetMapping(ApiEndpoints.Favorite.DETAILS)
    fun getFavoriteDetails(@AuthenticationPrincipal user: UserPrincipal): ResponseEntity<List<Restaurant>> =
        ResponseEntity.ok(userRestaurantService.getFavoriteDetails(user.userId))


    @PutMapping(ApiEndpoints.Favorite.MEMO)
    fun editMemoRestaurant(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable restaurantId: Int,
        @RequestBody request: UserRestaurantMemoRequest
    ): ResponseEntity<UserRestaurant> =
        ResponseEntity.ok(userRestaurantService.editMemoRestaurant(user.userId, restaurantId, request.memo))


    @GetMapping(ApiEndpoints.Favorite.MEMO)
    fun getMemoRestaurant(
        @AuthenticationPrincipal user: UserPrincipal,
        @PathVariable restaurantId: Int
    ): ResponseEntity<UserRestaurantMemoResponse> {
        val opt = userRestaurantService.getUserRestaurant(user.userId, restaurantId)
        val memo = opt?.userMemo ?: ""
        return ResponseEntity.ok(UserRestaurantMemoResponse(memo))
    }
    
}