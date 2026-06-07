package com.example.Community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResult {
    private TokenInfo token;        // 응답 바디 (accessToken, expiresIn)
    private String newRefreshToken;     // 리프레시 토큰 회전(RTR)이 발생한 경우에만 전달 (없으면 null)
}
