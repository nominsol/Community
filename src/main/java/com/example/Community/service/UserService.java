package com.example.Community.service;

import com.example.Community.domain.entity.File;
import com.example.Community.domain.entity.User;
import com.example.Community.domain.repository.FileRepository;
import com.example.Community.domain.repository.UserQueryRepository;
import com.example.Community.domain.repository.UserRepository;
import com.example.Community.dto.*;
import com.example.Community.exception.NotFoundException;
import com.example.Community.util.FileUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserQueryRepository userQueryRepository;
    private final FileRepository fileRepository;

    //회원가입
    @Transactional
    public SignupResponse createUser(SignupRequest signupRequest) {

        File profileImage = resolveProfileImage(signupRequest.getProfileImageUrl());

        User user = new User(
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getName(),
                profileImage
        );

        User savedUser = userRepository.save(user);
        return SignupResponse.of(savedUser.getId());
    }


    @Transactional(readOnly = true)
    public UserInfoResponse getUser(Long userId) {
        User user = userQueryRepository.findByIdWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        return UserInfoResponse.from(user);
    }

    @Transactional
    public void updateName(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userQueryRepository.findByIdWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        user.changeName(updateUserRequest.getName());
        applyProfileImage(user, updateUserRequest.getProfileImageUrl());
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // ========== Private Methods ==========

    private File resolveProfileImage(String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            return null;
        }

        // 1. 전체 URL에서 도메인을 떼고 상대 경로만 추출
        String relativePath = extractPathFromUrl(profileImageUrl);

        // 2. 추출된 상대 경로로 DB 조회
        return fileRepository.findByFilePath(relativePath)
                .orElseThrow(() -> new NotFoundException("PROFILE_IMAGE_NOT_FOUND"));
    }

    // URL에서 도메인을 제외한 경로(Path)만 추출하는 헬퍼 메서드
    private String extractPathFromUrl(String url) {
        try {
            // URI 파싱을 통해 path 부분만 가져옴
            URI uri = new URI(url);
            String path = uri.getPath();

            // 혹시 path가 null이면(잘못된 URL 등) 원본 반환
            return path != null ? path : url;

        } catch (URISyntaxException e) {
            // URL 형식이 아니라면(이미 상대경로이거나 잘못된 문자열) 그냥 원본 반환
            return url;
        }
    }

    // 프로필 이미지 적용
    private void applyProfileImage(User user, String requestedPath) {
        // 1. null 이면 제거
        if (requestedPath == null) {
            user.updateProfileImage(null);
            return;
        }

        // 프론트에서 온 전체 URL(http://...)을 DB용 상대 경로(/public/...)로 변환
        String relativePath = FileUtil.extractPathFromUrl(requestedPath);

        // 2. 현재 경로와 비교 (불필요한 갱신 방지)
        String currentPath = Optional.ofNullable(user.getProfileImage())
                .map(File::getFilePath)
                .orElse(null);

        // 둘 다 상대 경로인 상태에서 비교해야 정확함
        if (relativePath.equals(currentPath)) {
            return;
        }

        // 3. 파일 조회 (상대 경로로 조회)
        File newProfileImage = fileRepository.findByFilePath(relativePath)
                .orElseThrow(() -> new NotFoundException("PROFILE_IMAGE_NOT_FOUND"));

        user.updateProfileImage(newProfileImage);
    }

}
