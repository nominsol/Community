package com.example.Community.domain.repository;

import com.example.Community.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);  //게시글ID로 댓글 목록 조회
}
