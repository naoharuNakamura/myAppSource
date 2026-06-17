package com.zisakuapp.zisaku_backend.dto;

import lombok.Data;
import lombok.Setter;
import lombok.Getter;

@Data
@Getter
@Setter
public class UserRestaurantResponse {
    private String status; // "added" or "removed"

    public UserRestaurantResponse(String status) {
        this.status = status;
    }
}
