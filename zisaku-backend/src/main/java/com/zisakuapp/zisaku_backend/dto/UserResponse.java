package com.zisakuapp.zisaku_backend.dto;

import com.zisakuapp.zisaku_backend.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    private int userId;
    private String userName;
    private String userEmail;
    private String userPhone;

    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.userEmail = user.getUserEmail();
        this.userPhone = user.getUserPhone();
    }
}
