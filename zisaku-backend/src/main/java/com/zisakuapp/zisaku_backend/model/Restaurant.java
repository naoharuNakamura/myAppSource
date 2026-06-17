package com.zisakuapp.zisaku_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    private int restaurantId;
    private String restaurantName;
    private String restaurantImg;
    private Double restaurantRating;
    private String restaurantGenre;
    private String restaurantPriceRange;
    private String restaurantArea;
    private String restaurantOpenHour;
    private String restaurantCloseHour;
    private String restaurantAddress; // (adress → Address にスペルミスが直っていたら合わせておいてください)
    private String restaurantPhone;
    private String restaurantUrl;
    private String restaurantClosedDays;

    private boolean isFavorite;
}