package com.example.Community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {
    private Long userId;

    public static SignupResponse of(Long userId) {
        return new SignupResponse(
                userId
        );
    }
}
