package com.zisakuapp.zisaku_backend.service;

import com.zisakuapp.zisaku_backend.model.Restaurant;
import com.zisakuapp.zisaku_backend.model.UserRestaurant;
import com.zisakuapp.zisaku_backend.dto.UserRestaurantResponse;
import com.zisakuapp.zisaku_backend.mapper.RestaurantMapper;
import com.zisakuapp.zisaku_backend.mapper.UserRestaurantMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserRestaurantService {

    @Autowired
    private UserRestaurantMapper userRestaurantMapper;

    @Autowired
    private RestaurantMapper restaurantMapper;

    public List<UserRestaurant> getAllUserRestaurants() {
        return userRestaurantMapper.findAll();
    }

    public UserRestaurantResponse toggleFavorite(int userId, int restaurantId) {
        Optional<UserRestaurant> existing = userRestaurantMapper.findByIdUserIdAndIdRestaurantId(userId, restaurantId);

        if (existing.isPresent()) {
            UserRestaurant userRestaurant = existing.get();
            boolean isCurrentlyFavorite = Boolean.TRUE.equals(userRestaurant.getIsFavorite());

            if (isCurrentlyFavorite) {
                // お気に入り状態だったので解除する
                userRestaurant.setIsFavorite(false);

                // メモも空（またはnull）なら、不要なデータなのでレコードごと削除
                if (userRestaurant.getUserMemo() == null || userRestaurant.getUserMemo().trim().isEmpty()) {
                    userRestaurantMapper.deleteByIdUserIdAndIdRestaurantId(userId, restaurantId);
                } else {
                    // メモが残っている場合は更新して残す
                    userRestaurantMapper.upsert(userRestaurant);
                }
                return new UserRestaurantResponse("removed");

            } else {
                // メモだけ存在していてお気に入りではなかった場合 -> お気に入りONにする
                userRestaurant.setIsFavorite(true);
                userRestaurantMapper.upsert(userRestaurant);
                return new UserRestaurantResponse("added");
            }

        } else {
            // 全く新しいデータとしてお気に入り登録
            UserRestaurant userRestaurant = new UserRestaurant();
            UserRestaurant.UserRestaurantId id = new UserRestaurant.UserRestaurantId(userId, restaurantId);
            userRestaurant.setId(id);
            userRestaurant.setIsFavorite(true);
            System.out.println(userRestaurant);
            userRestaurantMapper.upsert(userRestaurant);
            return new UserRestaurantResponse("added");
        }
    }

    public UserRestaurant getMemoRestaurant(int userId, int restaurantId) {
        Optional<UserRestaurant> existing = userRestaurantMapper.findByIdUserIdAndIdRestaurantId(userId, restaurantId);
        return existing.orElse(null);
    }

    public UserRestaurant editMemoRestaurant(int userId, int restaurantId, String memo) {
        Optional<UserRestaurant> existing = userRestaurantMapper.findByIdUserIdAndIdRestaurantId(userId, restaurantId);

        UserRestaurant userRestaurant;
        if (existing.isPresent()) {
            userRestaurant = existing.get();
            userRestaurant.setUserMemo(memo);
        } else {
            userRestaurant = new UserRestaurant();
            UserRestaurant.UserRestaurantId id = new UserRestaurant.UserRestaurantId(userId, restaurantId);
            userRestaurant.setId(id);
            userRestaurant.setUserMemo(memo);
            userRestaurant.setIsFavorite(false);
        }
        System.out.println(userRestaurant);
        userRestaurantMapper.upsert(userRestaurant);
        return userRestaurant;
    }

    public List<Integer> getUserFavorites(int userId) {
        List<UserRestaurant> favorites = userRestaurantMapper.findByIdUserId(userId);
        System.out.println(favorites);
        return favorites.stream()
                .filter(fav -> Boolean.TRUE.equals(fav.getIsFavorite()))
                .map(fav -> fav.getId().getRestaurantId())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Restaurant> getFavoriteDetails(int userId) {
        List<UserRestaurant> favorites = userRestaurantMapper.findByIdUserId(userId);

        favorites.forEach(f -> System.out
                .println("DEBUG: Restaurant=" + f.getId().getRestaurantId() + ", IsFavorite=" + f.getIsFavorite()));

        List<Integer> ids = favorites.stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsFavorite()))
                .map(r -> r.getId().getRestaurantId())
                .toList();

        System.out.println("DEBUG: Filtered IDs=" + ids);

        if (ids.isEmpty()) {
            return List.of();
        }

        return restaurantMapper.findAllByIds(ids);
    }

    public Optional<UserRestaurant> getUserRestaurant(int userId, int restaurantId) {
        return userRestaurantMapper.findByIdUserIdAndIdRestaurantId(userId, restaurantId);
    }
}