package com.zisakuapp.zisaku_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RestaurantSearchRequest {
    private String restaurantName;
    private String restaurantRating;      // 修正：restaurant_rating → restaurantRating
    private String restaurantGenre;       // 修正：restaurant_genre → restaurantGenre
    private String restaurantPriceRange;  // 修正：restaurant_price_range → restaurantPriceRange
    private String restaurantArea;        // 修正：restaurant_area → restaurantArea
    private Boolean isAndSearch;

    private Double minRating;
    private Double maxRating;

    private String priceNum1;
    private String priceNum2;

    private String sort;
}
