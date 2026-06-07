package com.example.Community.domain.repository;

import com.example.Community.domain.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<User> findByEmailWithProfileImage(String email) {
        return entityManager.createQuery("""
            select u
            from User u
            left join fetch u.profileImage
            where u.email = :email
        """, User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<User> findByIdWithProfileImage(Long id) {
        return entityManager.createQuery("""
            select u
            from User u
            left join fetch u.profileImage
            where u.id = :id
        """, User.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }
}