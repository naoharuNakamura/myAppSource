package com.zisakuapp.zisaku_backend.mapper;

import com.zisakuapp.zisaku_backend.model.Restaurant;
import com.zisakuapp.zisaku_backend.dto.RestaurantSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface RestaurantMapper {
    List<String> findDistinctGenres();
    List<String> findDistinctPriceRanges();
    List<String> findDistinctAreas();
    List<Double> findDistinctRatings();
    List<Restaurant> findAll();
    List<Restaurant> findAllByIds(List<Integer> restaurantIds);
    Restaurant findByRestaurantId(int restaurantId);
    
    // 💡 動的検索用メソッド
    List<Restaurant> searchRestaurants(RestaurantSearchRequest request);
}