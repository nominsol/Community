package com.example.Community.controller;

import com.example.Community.auth.JwtProvider;
import com.example.Community.domain.entity.User;
import com.example.Community.domain.repository.UserRepository;
import com.example.Community.dto.*;
import com.example.Community.response.ApiResponse;
import com.example.Community.service.AuthService;
import com.example.Community.util.FileUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    // 로그인
    @PostMapping("/auth")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse httpResponse
    ) {
        // 로그인 처리
        LoginResult result = authService.login(loginRequest);

        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(14 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        // 쿠키를 응답 헤더에 추가
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("LOGIN_SUCCESS", result.getResponse()));
    }

    // 액세스 토큰 재발급
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<TokenInfo>> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpResponse
    ) {
        TokenResult result = authService.refreshAccessToken(refreshToken);

        // Refresh Token 회전 시 새 쿠키 세팅
        if (result.getNewRefreshToken() != null) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", result.getNewRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(14 * 24 * 60 * 60)
                    .sameSite("Lax")
                    .build();
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("TOKEN_REFRESH_SUCCESS", result.getToken()));
    }

    @GetMapping("/auth/check")
    public ResponseEntity<ApiResponse<AuthStatusResponse>> checkAuth(
            @AuthenticationPrincipal Long userId,
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        Long authenticatedUserId = userId;

        // 헤더 토큰이 없어서 userId가 null인 경우 쿠키로 검증
        if (authenticatedUserId == null && refreshToken != null && !refreshToken.isBlank()) {
            authenticatedUserId = jwtProvider.getUserId(refreshToken);
        }

        if (authenticatedUserId != null) {
            User user = userRepository.findById(authenticatedUserId).orElse(null);

            if (user != null) {
                // 프로필 이미지가 있다면 전체 URL로 변환, 없으면 null
                String profileUrl = user.getProfileImage() != null ?
                        FileUtil.toFullUrl(user.getProfileImage().getFilePath()) : null;

                AuthStatusResponse authData = AuthStatusResponse.of(
                        String.valueOf(user.getId()),
                        user.getEmail(),
                        user.getNickname(),
                        profileUrl
                );

                return ResponseEntity.ok(ApiResponse.of("AUTHENTICATED", authData));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.of("UNAUTHORIZED", null));
    }

    //로그아웃
    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken);

        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .path("/")
                .sameSite("Lax")
                .httpOnly(true)
                .secure(false)  //todo : https 적용 시 true로 변경하기
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ResponseEntity.ok(ApiResponse.of("LOGOUT_SUCCESS", null));
    }

}