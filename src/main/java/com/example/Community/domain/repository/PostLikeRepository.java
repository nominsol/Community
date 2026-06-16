package com.example.Community.domain.repository;

import com.example.Community.domain.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);  //유저ID랑 게시글ID로 좋아요 조회
}
