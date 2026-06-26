package com.example.backend.service

import com.example.backend.dto.restaurant.RestaurantSearchRequest
import com.example.backend.dto.restaurant.RestaurantSearchResponse
import com.example.backend.dto.restaurant.toRatingRange
import com.example.backend.dto.restaurant.toSearchResponse
import com.example.backend.exception.DomainException
import com.example.backend.mapper.RestaurantMapper
import com.example.backend.mapper.UserReviewMapper
import com.example.backend.model.Restaurant

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo
import com.google.maps.errors.OverQueryLimitException
import com.google.maps.errors.ZeroResultsException

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class RestaurantService(
    val restaurantMapper: RestaurantMapper,
    val userReviewMapper: UserReviewMapper,
    val geoContext: GeoApiContext
) {
    fun getGenres(): List<String> = restaurantMapper.findDistinctGenres()
    fun getPriceRanges(): List<String> = restaurantMapper.findDistinctPriceRanges()
    fun getAreas(): List<String> = restaurantMapper.findDistinctAreas()
    fun getRatings(): List<Double> = restaurantMapper.findDistinctRatings()
    fun getAllRestaurants(): List<Restaurant> = restaurantMapper.findAll()

    @Transactional
    fun getRestaurantDetail(restaurantId: Int): RestaurantSearchResponse {
        val restaurant: Restaurant =  restaurantMapper.findByRestaurantId(restaurantId) ?: throw DomainException.ResourceNotFound()
        if (restaurant.isGeocoded) {
            return restaurant.toSearchResponse()
        }

        runCatching {
            GeocodingApi.geocode(geoContext, restaurant.restaurantAddress).await()
        }.onSuccess { results ->
            val location = results.firstOrNull()?.geometry?.location
            if (location != null) {
                restaurant.latitude = location.lat
                restaurant.longitude = location.lng

            } else {
                throw DomainException.Restaurant.Location()
            }
            restaurant.isGeocoded = true
        }.onFailure { exception ->
            when (exception) {
                is ZeroResultsException -> {
                    restaurant.isGeocoded = true
                    throw DomainException.Restaurant.Address()
                }

                is OverQueryLimitException -> {
                    throw DomainException.Restaurant.Time()
                }

                else -> {
                    throw DomainException.Unknown("${exception.message}")
                }
            }
        }
        restaurantMapper.updateRestaurant(restaurant)
        println(restaurant
        )
        return restaurant.toSearchResponse()
    }

    private fun parseRatings(request: RestaurantSearchRequest): RestaurantSearchRequest {
        val (min, max) = request.restaurantRating?.toRatingRange() ?: return request

        return if (min != null && max != null) {
            request.copy(minRating = min, maxRating = max)
        } else {
            request
        }
    }

    fun searchRestaurants(request: RestaurantSearchRequest, page: Int, size: Int): PageInfo<RestaurantSearchResponse> {
        val parsedRequest = parseRatings(request)

        val pageNum = page.coerceAtLeast(1)
        val orderBy = mapOf(
            "restaurantRating,desc" to "restaurant_rating DESC",
            "restaurantRating,asc" to "restaurant_rating ASC",
            "restaurantId,asc" to "restaurant_id ASC"
        )[parsedRequest.sort] ?: "restaurant_id ASC"

        PageHelper.startPage<Any>(pageNum, size, orderBy)
        val restaurants = restaurantMapper.searchRestaurants(parsedRequest) ?: emptyList()
        val originalPageInfo = PageInfo(restaurants)
        val dtoList = restaurants.map { it.toSearchResponse() }

        return PageInfo<RestaurantSearchResponse>().apply {
            total = originalPageInfo.total
            this.pageNum = pageNum
            this.pageSize = size
            pages = originalPageInfo.pages
            this.size = restaurants.size
            list = dtoList
        }
    }

    @Transactional
    fun updateRestaurant(restaurantId: Int) {
        val avgRating = userReviewMapper.caluclateAverageRating(restaurantId) ?: 0.0
        val restaurant = restaurantMapper.findByRestaurantIdForUpdate(restaurantId)
            ?: throw DomainException.ResourceNotFound()
        restaurant.restaurantRating = avgRating
        restaurantMapper.updateRestaurant(restaurant)
    }

    fun checkRestaurantExist(restaurantId: Int): Boolean = restaurantMapper.checkRestaurantExists(restaurantId)
}