package com.example.Community.controller;

import com.example.Community.dto.PostRequestDto;
import com.example.Community.dto.PostResponseDto;
import com.example.Community.response.ApiResponse;
import com.example.Community.service.PostService;
import com.example.Community.service.PostStatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostStatService postStatService;

    @PostMapping()
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PostRequestDto request
    ) {
        PostResponseDto result = postService.createPost(userId, request);

        //통계 생성
        postStatService.createPostStat(result.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("POST_CREATED", result));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(@PathVariable("postId") Long postId) {
        PostResponseDto result = postService.getPost(postId);

        //조회수 통계 추가
        postStatService.increasePostStatView(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_RETRIEVED", result));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostRequestDto request
    ) {
        PostResponseDto result = postService.updatePost(postId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_UPDATED", result));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable("postId") Long postId
    ) {
        postService.deletePost(postId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("POST_DELETED", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponseDto>>> getPosts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<PostResponseDto> result = postService.getPosts(offset, limit);
        return ResponseEntity.ok(
                ApiResponse.of("POSTS_RETRIEVED", result));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PostResponseDto>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "recent") String sort
    ) {
        List<PostResponseDto> result = postService.searchPosts(keyword, offset, limit, sort);
        return ResponseEntity.ok(
                ApiResponse.of("POSTS_SEARCHED", result));
    }
}