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
    private String nickname;
    private String profileImageUrl;

    public static UserInfoResponse of(
            Long userId,
            String email,
            String nickname,
            String profileImageUrl
    ) {
        return new UserInfoResponse(
                userId, email, nickname, profileImageUrl
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
                user.getNickname(),
                fullProfileUrl
        );
    }
}
