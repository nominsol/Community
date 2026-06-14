package com.example.Community.dto;

import com.example.Community.domain.entity.Post;
import com.example.Community.domain.entity.PostLike;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostLikeResponseDto {
    private Long id;
    private Long userId;
    private Long postId;

    public PostLikeResponseDto(PostLike postLike) {
        this.id = postLike.getId();
        this.userId = postLike.getUser().getId();
        this.postId = postLike.getPost().getId();
    }

}
