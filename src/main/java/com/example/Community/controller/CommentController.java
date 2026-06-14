package com.example.Community.controller;

import com.example.Community.dto.CommentRequestDto;
import com.example.Community.dto.CommentResponseDto;
import com.example.Community.dto.PostResponseDto;
import com.example.Community.response.ApiResponse;
import com.example.Community.service.CommentService;
import com.example.Community.service.PostStatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.annotation.JsonValueInstantiator;

@RestController
@RequestMapping("/posts/{post_id}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostStatService postStatService;

    @PostMapping()
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequestDto request
    ) {
        CommentResponseDto result = commentService.createComment(userId, postId, request);

        //댓글수 통계 추가
        postStatService.increasePostStatComment(postId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("POST_CREATED", result));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDto request
    ) {
        CommentResponseDto result = commentService.updateComment(commentId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("COMMENT_UPDATED", result));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @PathVariable Long postId
    ) {
        commentService.deleteComment(commentId);

        //댓글수 통계 추가
        postStatService.decreasePostStatComment(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("COMMENT_DELETED", null));
    }
}
