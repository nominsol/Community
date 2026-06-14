package com.example.Community.service;

import com.example.Community.domain.entity.Post;
import com.example.Community.domain.entity.PostStat;
import com.example.Community.domain.repository.PostRepository;
import com.example.Community.domain.repository.PostStatRepository;
import com.example.Community.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class PostStatService {

    private final PostStatRepository postStatRepository;
    private final PostRepository postRepository;

    @Transactional
    public void createPostStat(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        PostStat postStat = new PostStat(
                post,
                0L,
                0L,
                0L
        );

        PostStat savedPostStat = postStatRepository.save(postStat);
    }

    @Transactional
    public void increasePostStatView(Long postId) {
        PostStat postStat = postStatRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        postStat.increaseViewCount();
    }

    @Transactional
    public void increasePostStatLike(Long postId) {
        PostStat postStat = postStatRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        postStat.increaseLikeCount();
    }

    @Transactional
    public void decreasePostStatLike(Long postId) {
        PostStat postStat = postStatRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        postStat.decreaseLikeCount();
    }

    @Transactional
    public void increasePostStatComment(Long postId) {
        PostStat postStat = postStatRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        postStat.increaseCommentCount();
    }

    @Transactional
    public void decreasePostStatComment(Long postId) {
        PostStat postStat = postStatRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        postStat.decreaseCommentCount();
    }
}
