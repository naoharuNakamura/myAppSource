package com.zisakuapp.zisaku_backend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int userId;
    private String userPassword;
    private String userName;
    private String userEmail;
    private String userPhone;
}