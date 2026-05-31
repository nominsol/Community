package com.example.Community.domain.repository;

import com.example.Community.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.LongSummaryStatistics;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Long userId);
}
