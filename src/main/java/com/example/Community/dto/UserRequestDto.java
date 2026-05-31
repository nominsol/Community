package com.example.Community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    private String email;
    private String password;
    private String name;
    private String image;
}
