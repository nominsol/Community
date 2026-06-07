package com.example.Community.domain.repository;


import com.example.Community.domain.entity.User;

import java.util.Optional;

public interface UserQueryRepository {
    Optional<User> findByEmailWithProfileImage(String email);
    Optional<User> findByIdWithProfileImage(Long id);
}