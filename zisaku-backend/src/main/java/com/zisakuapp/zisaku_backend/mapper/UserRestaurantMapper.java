package com.zisakuapp.zisaku_backend.mapper;

import com.zisakuapp.zisaku_backend.model.UserRestaurant;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;
import java.util.List;

@Mapper
public interface UserRestaurantMapper {
    List<UserRestaurant> findAll();

    List<UserRestaurant> findByIdUserId(int userId);
    
    Optional<UserRestaurant> findByIdUserIdAndIdRestaurantId(int userId, int restaurantId);
    
    void upsert(UserRestaurant userRestaurant);

    void deleteByIdUserIdAndIdRestaurantId(int userId, int restaurantId);
}