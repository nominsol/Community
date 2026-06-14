package com.example.Community.service;

import com.example.Community.domain.entity.Comment;
import com.example.Community.domain.entity.Post;
import com.example.Community.domain.entity.User;
import com.example.Community.domain.repository.CommentRepository;
import com.example.Community.domain.repository.PostRepository;
import com.example.Community.domain.repository.UserRepository;
import com.example.Community.dto.CommentRequestDto;
import com.example.Community.dto.CommentResponseDto;
import com.example.Community.dto.PostResponseDto;
import com.example.Community.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponseDto createComment(Long userId, Long postId, CommentRequestDto request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        Comment comment = new Comment(
                request.getContent(),
                user,
                post
        );

        Comment savedComment = commentRepository.save(comment);
        return new CommentResponseDto(savedComment);
    }

    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto request){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND"));

        comment.changeContent(request.getContent());

        return new CommentResponseDto(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) { commentRepository.deleteById(commentId); }
}
