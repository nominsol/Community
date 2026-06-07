package com.example.Community.service;

import com.example.Community.domain.entity.Post;
import com.example.Community.domain.entity.User;
import com.example.Community.domain.repository.PostRepository;
import com.example.Community.domain.repository.UserRepository;
import com.example.Community.dto.PostRequestDto;
import com.example.Community.dto.PostResponseDto;
import com.example.Community.exception.AuthorizedException;
import com.example.Community.exception.BusinessException;
import com.example.Community.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponseDto createPost(Long userId, PostRequestDto request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                author
        );

        Post savedPost = postRepository.save(post);
        return new PostResponseDto(savedPost);
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));
        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        post.changeTitle(request.getTitle());
        post.changeContent(request.getContent());

        return new PostResponseDto(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}
