package com.dailyq.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private boolean isNewUser;
}
