package com.example.Community.controller;

import com.example.Community.dto.*;
import com.example.Community.response.ApiResponse;
import com.example.Community.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

//    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
//        Cookie cookie = new Cookie("refreshToken", token);
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(60 * 60 * 24 * 14); // 14일
//        response.addCookie(cookie);
//    }
//
//    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
//        if (request.getCookies() == null) {
//            throw new com.example.Community.exception.AuthorizedException("missing_refresh_token");
//        }
//        for (Cookie cookie : request.getCookies()) {
//            if ("refreshToken".equals(cookie.getName())) {
//                return cookie.getValue();
//            }
//        }
//        throw new com.example.Community.exception.AuthorizedException("missing_refresh_token");
//    }
}