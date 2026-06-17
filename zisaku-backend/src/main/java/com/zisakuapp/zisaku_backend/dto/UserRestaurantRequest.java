package com.zisakuapp.zisaku_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserRestaurantRequest {
    private int userId;
    private int restaurantId;
}
