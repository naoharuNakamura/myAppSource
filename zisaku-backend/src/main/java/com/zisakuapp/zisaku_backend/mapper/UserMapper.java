package com.zisakuapp.zisaku_backend.mapper; // repositoryからmapperへ変更推奨

import com.zisakuapp.zisaku_backend.model.User;
import org.apache.ibatis.annotations.*;
import java.util.Optional;
import java.util.List;

@Mapper
public interface UserMapper {
    Optional<User> findByUserEmail(@Param("userEmail") String userEmail);

    Optional<User> findByUserId(int userId);

    Boolean existsByUserEmail(String userEmail);

    void insert(User user);

    void update(User user);

    int countAllUsers();

    List<String> getAllEmails();
}