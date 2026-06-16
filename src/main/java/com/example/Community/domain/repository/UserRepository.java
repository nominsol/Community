package com.example.Community.domain.repository;

import com.example.Community.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email); // 이메일 중복 체크
    boolean existsByNickname(String nickname);   // 이름 중복 체크
}
