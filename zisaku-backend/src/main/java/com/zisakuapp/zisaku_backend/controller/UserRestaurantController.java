package com.zisakuapp.zisaku_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zisakuapp.zisaku_backend.model.Restaurant;
import com.zisakuapp.zisaku_backend.model.UserRestaurant;
import com.zisakuapp.zisaku_backend.service.UserRestaurantService;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/t_user_restaurant")
public class UserRestaurantController {

    @Autowired
    private UserRestaurantService userRestaurantService;

    @GetMapping("/result")
    public List<UserRestaurant> getAllUserRestaurants() {
        return userRestaurantService.getAllUserRestaurants();
    }

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFavorite(@RequestBody Map<String, Object> payload) {
        int userId = ((Number) payload.get("userId")).intValue();
        int restaurantId = ((Number) payload.get("restaurantId")).intValue();

        var response = userRestaurantService.toggleFavorite(userId, restaurantId);
        return ResponseEntity.ok(response);
    }

    // ★ ("userId") を明記
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Integer>> getUserFavorites(@PathVariable("userId") int userId) {
        List<Integer> restaurantIds = userRestaurantService.getUserFavorites(userId);
        return ResponseEntity.ok(restaurantIds);
    }

    // ★ ("userId") を明記
    @GetMapping("/user/{userId}/details")
    public ResponseEntity<List<Restaurant>> getFavoriteDetails(@PathVariable("userId") int userId) {
        List<Restaurant> result = userRestaurantService.getFavoriteDetails(userId);
        return ResponseEntity.ok(result);
    }

    // ★ フロントの types.ts の EDIT_MEMO のURLと完全に一致させる
    @PostMapping("/user/{userId}/restaurant/{restaurantId}")
    public ResponseEntity<?> editMemoRestaurant(
            @PathVariable("userId") int userId,
            @PathVariable("restaurantId") int restaurantId,
            @RequestBody Map<String, Object> payload) {

        // userId は URL（@PathVariable）から取得できるため、payload から取り出す処理は削除
        String memo = (String) payload.get("memo");

        UserRestaurant updated = userRestaurantService.editMemoRestaurant(userId, restaurantId, memo);
        return ResponseEntity.ok(updated);
    }

    // ★ フロントの api.ts に合わせ、パスに userId と restaurantId の両方を含める形に修正
    // ★ 各 @PathVariable にも名前を明記
    @GetMapping("/user/{userId}/restaurant/{restaurantId}")
    public ResponseEntity<?> getMemoRestaurant(
            @PathVariable("userId") int userId,
            @PathVariable("restaurantId") int restaurantId) {

        var opt = userRestaurantService.getUserRestaurant(userId, restaurantId);
        if (opt.isPresent()) {
        // メモを取得し、nullであれば空文字に変換する
            String memo = opt.get().getUserMemo();
            return ResponseEntity.ok(Map.of("memo", memo != null ? memo : ""));
        } else {
            return ResponseEntity.ok(Map.of("memo", ""));
        }
    }
}