package com.example.Community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResult {
    private LoginResponse response;  //응답 바디용
    private String refreshToken;    //쿠키용
}
