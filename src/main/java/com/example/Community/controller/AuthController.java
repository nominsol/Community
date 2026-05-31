package com.example.Community.controller;

import com.example.Community.dto.LoginRequest;
import com.example.Community.dto.LoginResult;
import com.example.Community.dto.TokenResult;
import com.example.Community.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResult result = authService.login(request);

        setRefreshTokenCookie(response, result.getRefreshToken());

        Map<String, Object> body = Map.of(
                "data", Map.of(
                        "user_id", result.getResponse().getUser().getId(),
                        "jwt", result.getResponse().getToken().getAccessToken()
                )
        );
        return ResponseEntity.ok(body);
    }

    @PostMapping("/users/token/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        TokenResult result = authService.refresh(refreshToken);

        if (result.getNewRefreshToken() != null) {
            setRefreshTokenCookie(response, result.getNewRefreshToken());
        }

        Map<String, Object> body = Map.of(
                "data", Map.of(
                        "access_token", result.getToken().getAccessToken(),
                        "expires_in", result.getToken().getExpiresIn()
                )
        );
        return ResponseEntity.ok(body);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14); // 14일
        response.addCookie(cookie);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new com.example.Community.exception.AuthorizedException("missing_refresh_token");
        }
        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new com.example.Community.exception.AuthorizedException("missing_refresh_token");
    }
}