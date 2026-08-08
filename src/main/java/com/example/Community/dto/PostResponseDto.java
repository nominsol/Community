package com.example.Community.dto;

import com.example.Community.domain.entity.Post;
import com.example.Community.domain.entity.PostStat;
import com.example.Community.util.FileUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String nickname;
    private String profileImage;
    private LocalDateTime createdAt;
    private List<String> imageUrls;

    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private boolean isLiked;

    public PostResponseDto(Post post) {
        this(post, null, false);
    }

    public PostResponseDto(Post post, PostStat postStat, boolean isLiked) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.authorId = post.getAuthor().getId();
        this.nickname = post.getAuthor().getNickname();
        this.createdAt = post.getCreatedDate();

        this.profileImage = post.getAuthor().getProfileImage() != null
                ? FileUtil.toFullUrl(post.getAuthor().getProfileImage().getFilePath())
                : null;

        this.imageUrls = post.getImages().stream()
                .map(file -> FileUtil.toFullUrl(file.getFilePath()))
                .collect(Collectors.toList());

        this.viewCount = postStat != null ? postStat.getViewCount() : 0L;
        this.likeCount = postStat != null ? postStat.getLikeCount() : 0L;
        this.commentCount = postStat != null ? postStat.getCommentCount() : 0L;
        this.isLiked = isLiked;
    }
}