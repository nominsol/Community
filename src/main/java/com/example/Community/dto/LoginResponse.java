package com.example.Community.dto;

import com.example.Community.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private User user;
    private TokenInfo token;

    public static LoginResponse of(
            User user,
            String accessToken,
            long expiresIn
    ) {
        return new LoginResponse(
                user,
                new TokenInfo(accessToken, expiresIn)
        );
    }
}
