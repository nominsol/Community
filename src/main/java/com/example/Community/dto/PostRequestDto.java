package com.example.Community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String image;
}
