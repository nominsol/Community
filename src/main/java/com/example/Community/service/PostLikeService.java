package com.example.Community.service;

import com.example.Community.domain.entity.Post;
import com.example.Community.domain.entity.PostLike;
import com.example.Community.domain.entity.User;
import com.example.Community.domain.repository.PostLikeRepository;
import com.example.Community.domain.repository.PostRepository;
import com.example.Community.domain.repository.UserRepository;
import com.example.Community.dto.PostLikeResponseDto;
import com.example.Community.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostLikeResponseDto createPostLike(Long userId, Long postId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        PostLike postLike = new PostLike(
                user,
                post
        );
        PostLike savedPostLike = postLikeRepository.save(postLike);
        return new PostLikeResponseDto(savedPostLike);
    }

    @Transactional
    public void deletePostLikeByUserAndPost(Long userId, Long postId) {
        PostLike postLike = postLikeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new NotFoundException("POSTLIKE_NOT_FOUND"));
        postLikeRepository.delete(postLike);
    }
}
