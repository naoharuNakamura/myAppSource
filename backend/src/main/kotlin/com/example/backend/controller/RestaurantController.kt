package com.example.backend.controller

import com.example.backend.constants.ApiEndpoints
import com.example.backend.dto.restaurant.RestaurantSearchRequest
import com.example.backend.dto.restaurant.RestaurantSearchResponse
import com.example.backend.model.Restaurant
import com.example.backend.service.RestaurantService
import com.github.pagehelper.PageInfo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiEndpoints.Restaurant.ROOT)
class RestaurantController(
    val restaurantService: RestaurantService
) {
    @GetMapping(ApiEndpoints.Restaurant.GENRES)
    fun getGenres(): List<String> = restaurantService.getGenres()

    @GetMapping(ApiEndpoints.Restaurant.PRICE_RANGES)
    fun getPriceRanges(): List<String> = restaurantService.getPriceRanges()

    @GetMapping(ApiEndpoints.Restaurant.RATINGS)
    fun getRatings(): List<Double> = restaurantService.getRatings()

    @GetMapping(ApiEndpoints.Restaurant.AREAS)
    fun getAreas(): List<String> = restaurantService.getAreas()

    @GetMapping(ApiEndpoints.Restaurant.ALL)
    fun getAllRestaurants(): List<Restaurant> = restaurantService.getAllRestaurants()

    @GetMapping(ApiEndpoints.Restaurant.SEARCH)
    fun searchRestaurants(
        @ModelAttribute request: RestaurantSearchRequest,
        @RequestParam(name = "page", defaultValue = "1") page: Int,
        @RequestParam(name = "size", defaultValue = "50") size: Int
    ): PageInfo<RestaurantSearchResponse> = restaurantService.searchRestaurants(request, page, size)

    @GetMapping(ApiEndpoints.Restaurant.DETAIL)
    fun getRestaurantDetail(@PathVariable restaurantId: Int): RestaurantSearchResponse = restaurantService.getRestaurantDetail(restaurantId)

}