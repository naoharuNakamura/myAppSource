package com.zisakuapp.zisaku_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class UserSignupRequest {
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userPhone;
}