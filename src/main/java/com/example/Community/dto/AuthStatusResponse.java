package com.example.Community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthStatusResponse {

    private String userId;
    private String email;
    private String name;
    private String profileImageUrl;

    public static AuthStatusResponse of(
            String userId,
            String email,
            String name,
            String profileImageUrl
    ) {
        return new AuthStatusResponse(
                userId,
                email,
                name,
                profileImageUrl
        );
    }
}