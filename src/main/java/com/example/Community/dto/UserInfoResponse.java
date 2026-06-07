package com.example.Community.dto;

import com.example.Community.domain.entity.File;
import com.example.Community.domain.entity.User;
import com.example.Community.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;
    private String email;
    private String name;
    private String profileImageUrl;

    public static UserInfoResponse of(
            Long userId,
            String email,
            String name,
            String profileImageUrl
    ) {
        return new UserInfoResponse(
                userId, email, name, profileImageUrl
        );
    }

    public static UserInfoResponse from(User user) {
        String fullProfileUrl = Optional.ofNullable(user.getProfileImage())
                .map(File::getFilePath)
                .map(FileUtil::toFullUrl)
                .orElse(null);

        return of(
                user.getId(),
                user.getEmail(),
                user.getName(),
                fullProfileUrl
        );
    }
}
