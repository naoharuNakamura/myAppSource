package com.zisakuapp.zisaku_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor    // ← Spring BootのJSON変換用（保険）
public class LoginResponse {
    private String token;
    private UserResponse user;

    // 全てのフィールドを初期化するコンストラクタ
    public LoginResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }
}