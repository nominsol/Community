package com.example.Community.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.Community.domain.entity.File;
import com.example.Community.domain.repository.FileRepository;
import com.example.Community.exception.BusinessException;
import com.example.Community.exception.InvalidFileException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final AmazonS3 amazonS3; // AWS S3 클라이언트

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");

    // 프로필 이미지 업로드
    public File uploadProfileImage(MultipartFile file, Long userId) {
        return uploadToS3(file, userId, "profile");
    }

    // 게시글 파일 업로드
    public File uploadAttachFile(MultipartFile file, Long userId) {
        return uploadToS3(file, userId, "post");
    }

    private File uploadToS3(MultipartFile file, Long userId, String folder) {
        String extension = extractAndValidateExtension(file);
        String filename = generateFilename(folder, extension);

        String s3Key = "upload/" + folder + "/" + filename;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3.putObject(bucket, s3Key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new BusinessException("S3_UPLOAD_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String dbFilePath = "/" + s3Key;

        return fileRepository.save(folder.equals("profile") ?
                File.createProfileImage(dbFilePath, userId) :
                File.createPostAttachment(dbFilePath, userId));
    }

    private String extractAndValidateExtension(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new InvalidFileException(file.getName(), "FILE_NAME_REQUIRED");
        }
        String extension = StringUtils.getFilenameExtension(originalName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidFileException(file.getName(), "INVALID_FILE_EXTENSION");
        }
        return extension;
    }

    private String generateFilename(String prefix, String extension) {
        return prefix + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;
    }
}