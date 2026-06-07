package com.example.Community.domain.repository;

import com.example.Community.domain.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByFilePath(String filePath);
}