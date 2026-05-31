package com.example.Community.service;

import com.example.Community.domain.entity.Post;
import com.example.Community.domain.entity.User;
import com.example.Community.domain.repository.PostRepository;
import com.example.Community.domain.repository.UserRepository;
import com.example.Community.dto.PostRequestDto;
import com.example.Community.dto.PostResponseDto;
import com.example.Community.exception.AuthorizedException;
import com.example.Community.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponseDto createPost(Long userId, PostRequestDto request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                request.getImage(),
                author
        );

        Post savedPost = postRepository.save(post);
        return new PostResponseDto(savedPost);
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, Long userId, PostRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("already_deleted", HttpStatus.NOT_FOUND));

        //작성자 본인만 수정 가능
        if (!post.getAuthor().getId().equals(userId)) {
            throw new AuthorizedException("forbidden");
        }

        post.changeTitle(request.getTitle());
        post.changeContent(request.getContent());
        post.changeImage(request.getImage());
        return new PostResponseDto(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("already_deleted", HttpStatus.NOT_FOUND));

        //작성자 본인만 삭제 가능
        if (!post.getAuthor().getId().equals(userId)) {
            throw new AuthorizedException("forbidden");
        }

        postRepository.delete(post);
    }
}
