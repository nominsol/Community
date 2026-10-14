package com.example.Community.domain.repository;

import com.example.Community.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);  //게시글ID로 댓글 목록 조회

    // 댓글 작성자와 프로필 이미지를 한 번에 조회 (N+1 방지)
    @Query("""
            select c from Comment c
            join fetch c.user u
            left join fetch u.profileImage
            join fetch c.post
            where c.post.id = :postId
            """)
    List<Comment> findByPostIdWithUser(@Param("postId") Long postId);
}
