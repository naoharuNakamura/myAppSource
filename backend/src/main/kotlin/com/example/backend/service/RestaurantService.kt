package com.example.backend.service

import com.example.backend.dto.restaurant.RestaurantSearchRequest
import com.example.backend.dto.restaurant.RestaurantSearchResponse
import com.example.backend.dto.restaurant.toRatingRange
import com.example.backend.dto.restaurant.toSearchResponse
import com.example.backend.mapper.RestaurantMapper
import com.example.backend.model.Restaurant

import com.github.pagehelper.PageHelper
import com.github.pagehelper.PageInfo

import org.springframework.stereotype.Service


@Service
class RestaurantService(
    val restaurantMapper: RestaurantMapper
) {
    fun getGenres(): List<String> = restaurantMapper.findDistinctGenres()
    fun getPriceRanges(): List<String> = restaurantMapper.findDistinctPriceRanges()
    fun getAreas(): List<String> = restaurantMapper.findDistinctAreas()
    fun getRatings(): List<Double> = restaurantMapper.findDistinctRatings()
    fun getAllRestaurants(): List<Restaurant> = restaurantMapper.findAll()
    fun getRestaurantDetail(restaurantId: Int): RestaurantSearchResponse {
        val restaurant: Restaurant = restaurantMapper.findByRestaurantId(restaurantId)
            ?: throw RuntimeException("NO such restaurant: $restaurantId")
        return restaurant.toSearchResponse()
    }

    private fun parseRatings(request: RestaurantSearchRequest): RestaurantSearchRequest {
        return request.restaurantRating?.let { rating ->
            val (min, max) = rating.toRatingRange()

            if (min != null && max != null) {
                // 値が取れたら copy() で minRating と maxRating を上書きした新オブジェクトを返す
                request.copy(minRating = min, maxRating = max)
            } else {
                request
            }
        } ?: request // nullの場合は元のrequestをそのまま返す
    }

    fun searchRestaurants(request: RestaurantSearchRequest, page: Int, size: Int): PageInfo<RestaurantSearchResponse> {
        // 💡 修正: パース済みの新しいリクエストを受け取る
        val parsedRequest = parseRatings(request)

        val pageNum = if (page <= 0) 1 else page
        val orderBy = when (parsedRequest.sort) { // parsedRequest を使用
            "restaurantRating,desc" -> "restaurant_rating DESC"
            "restaurantRating,asc" -> "restaurant_rating ASC"
            "restaurantId,asc" -> "restaurant_id ASC"
            else -> "restaurant_id ASC"
        }

        PageHelper.startPage<Any>(pageNum, size, orderBy)

        // 💡 修正: Mapper には parsedRequest を渡す
        val restaurants = restaurantMapper.searchRestaurants(parsedRequest) ?: emptyList()
        val originalPageInfo = PageInfo(restaurants)
        val dtoList = restaurants.map { it.toSearchResponse() }

        return PageInfo<RestaurantSearchResponse>().apply {
            list = dtoList
            total = originalPageInfo.total
            this.pageNum = pageNum
            pageSize = size
            pages = originalPageInfo.pages
            this.size = dtoList.size
        }
    }
}