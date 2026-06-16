package com.example.Community.controller;

import com.example.Community.domain.entity.File;
import com.example.Community.dto.FileUploadResponse;
import com.example.Community.response.ApiResponse;
import com.example.Community.service.FileService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 회원가입 전 호출될 수 있으므로 @AuthenticationPrincipal을 필수가 아니게 처리
    @PostMapping("/users/upload/profile-image")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadProfileImage(
            @AuthenticationPrincipal Long userId,
            @RequestPart("file") MultipartFile file
    ) throws FileUploadException {
        File savedFile = fileService.uploadProfileImage(file, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("PROFILE_IMAGE_UPLOADED", FileUploadResponse.from(savedFile)));
    }

    // 게시글 이미지 업로드 추가
    @PostMapping("/posts/upload/attach-file")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadAttachFile(
            @AuthenticationPrincipal Long userId,
            @RequestPart("file") MultipartFile file
    ) throws FileUploadException {
        File savedFile = fileService.uploadAttachFile(file, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("ATTACHMENT_UPLOADED", FileUploadResponse.from(savedFile)));
    }
}