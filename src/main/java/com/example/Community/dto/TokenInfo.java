package com.example.Community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenInfo {
    private String accessToken;
    private long expiresIn;  //액세스 토큰의 남은 유효 시간
}
