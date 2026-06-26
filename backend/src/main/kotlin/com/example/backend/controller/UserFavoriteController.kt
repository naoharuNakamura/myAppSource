package com.example.backend.controller

import com.example.backend.constants.ApiEndpoints
import com.example.backend.dto.userfavorite.UserFavoriteRequest
import com.example.backend.dto.userfavorite.UserFavoriteResponse
import com.example.backend.model.Restaurant
import com.example.backend.model.UserFavorite
import com.example.backend.service.UserFavoriteService
import com.example.backend.model.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiEndpoints.Favorite.ROOT)
class UserFavoriteController(
    private val userFavoriteService: UserFavoriteService
) {

    @PostMapping(ApiEndpoints.Favorite.ITEM)
    fun toggleFavorite(
        @AuthenticationPrincipal user: UserPrincipal,
        @RequestBody request: UserFavoriteRequest   // 型安全なDTOで受け取る
    ): UserFavoriteResponse  = userFavoriteService.toggleFavorite(user.userId, request.restaurantId)

    @GetMapping(ApiEndpoints.Favorite.LIST)
    fun getUserFavorites(@AuthenticationPrincipal user: UserPrincipal): List<Int> = userFavoriteService.getUserFavorites(user.userId)
    
    @GetMapping(ApiEndpoints.Favorite.DETAILS)
    fun getFavoriteDetails(@AuthenticationPrincipal user: UserPrincipal): List<Restaurant> = userFavoriteService.getFavoriteDetails(user.userId)
}