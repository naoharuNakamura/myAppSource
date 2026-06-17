package com.zisakuapp.zisaku_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRestaurant {

    private UserRestaurantId id;
    @JsonProperty("isFavorite")
    private boolean isFavorite = false;
    // 明示的に setter を定義する
    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
    
    // getter も boolean の規約に合わせる（Lombokが作らない場合）
    public boolean getIsFavorite() {
        return this.isFavorite;
    }
    private String userMemo;

    @Data
    public static class UserRestaurantId implements Serializable {
        private int userId;
        private int restaurantId;

        public UserRestaurantId() {}
        public UserRestaurantId(int userId, int restaurantId) {
            this.userId = userId;
            this.restaurantId = restaurantId;
        }
    }
}