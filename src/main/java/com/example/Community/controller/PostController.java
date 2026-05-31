package com.example.Community.controller;

import com.example.Community.dto.PostRequestDto;
import com.example.Community.dto.PostResponseDto;
import com.example.Community.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, Object>> createPost(
            HttpServletRequest httpRequest,
            @RequestBody PostRequestDto request
    ) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        PostResponseDto created = postService.createPost(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("data", Map.of("post_id", created.getId())));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable Long postId) {
        PostResponseDto post = postService.getPost(postId);
        return ResponseEntity.ok(Map.of("data", post));
    }

    @PatchMapping("/{postId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> updatePost(
            @PathVariable Long postId,
            HttpServletRequest httpRequest,
            @RequestBody PostRequestDto request
    ) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        PostResponseDto updated = postService.updatePost(postId, userId, request);
        return ResponseEntity.ok(Map.of("data", Map.of("post_id", updated.getId())));
    }

    @DeleteMapping("/{postId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> deletePost(
            @PathVariable Long postId,
            HttpServletRequest httpRequest
    ) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        postService.deletePost(postId, userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Map.of("message", "success", "data", (Object) null));
    }
}