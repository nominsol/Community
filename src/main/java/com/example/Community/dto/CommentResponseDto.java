package com.example.Community.dto;

import com.example.Community.domain.entity.Comment;
import com.example.Community.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedDate;
    private Long postId;
    private AuthorInfo author;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.postId = comment.getPost().getId();

        String profileUrl = comment.getUser().getProfileImage() != null
                ? FileUtil.toFullUrl(comment.getUser().getProfileImage().getFilePath())
                : null;

        this.author = new AuthorInfo(
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                profileUrl
        );
    }

    @Getter
    @AllArgsConstructor
    public static class AuthorInfo {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
    }
}