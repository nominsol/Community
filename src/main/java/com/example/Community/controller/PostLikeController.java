package com.example.Community.controller;

import com.example.Community.dto.PostLikeResponseDto;
import com.example.Community.response.ApiResponse;
import com.example.Community.service.PostLikeService;
import com.example.Community.service.PostStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{post_id}/likes")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final PostStatService postStatService;

    @PostMapping()
    public ResponseEntity<ApiResponse<PostLikeResponseDto>> createPostLike(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId
    ) {
        PostLikeResponseDto result = postLikeService.createPostLike(userId, postId);

        //좋아요수 통계 추가
        postStatService.increasePostStatLike(postId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("POSTLIKE_CREATED", result));
    }

    @DeleteMapping("/{postLikeId}")
    public ResponseEntity<ApiResponse<Void>> deletePostLike(
            @PathVariable Long postLikeId,
            @PathVariable Long postId
    ) {
        postLikeService.deletePostLike(postLikeId);

        //좋아요수 통계 추가
        postStatService.decreasePostStatLike(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POSTLIKE_DELETED", null));
    }
}
