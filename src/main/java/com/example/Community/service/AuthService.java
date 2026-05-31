package com.example.Community.service;

import com.example.Community.auth.JwtProvider;
import com.example.Community.domain.entity.RefreshToken;
import com.example.Community.domain.entity.User;
import com.example.Community.domain.repository.RefreshTokenRepository;
import com.example.Community.domain.repository.UserRepository;
import com.example.Community.dto.LoginRequest;
import com.example.Community.dto.LoginResponse;
import com.example.Community.dto.LoginResult;
import com.example.Community.dto.TokenInfo;
import com.example.Community.dto.TokenResult;
import com.example.Community.exception.AuthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthorizedException("non_existent_user"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new AuthorizedException("non_existent_user");
        }

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getName());
        long expiresIn = jwtProvider.getAccessTokenValidityInMilliseconds();

        // 기존 Refresh Token 삭제 후 새로 발급
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        refreshTokenRepository.deleteByUserId(user.getId());

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProvider.getRefreshTokenExpSeconds());
        refreshTokenRepository.save(new RefreshToken(refreshToken, user.getId(), expiresAt));

        LoginResponse response = LoginResponse.of(user, accessToken, expiresIn);
        return new LoginResult(response, refreshToken);
    }

    @Transactional
    public TokenResult refresh(String rawRefreshToken) {
        // DB에서 토큰 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new AuthorizedException("invalid_refresh_token"));

        // DB 기준 만료 검사
        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new AuthorizedException("expired_refresh_token");
        }

        // JWT 서명/만료 검증
        jwtProvider.parse(rawRefreshToken);

        Long userId = storedToken.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthorizedException("non_existent_user"));

        String newAccessToken = jwtProvider.createAccessToken(userId, user.getEmail(), user.getName());
        long expiresIn = jwtProvider.getAccessTokenValidityInMilliseconds();

        // RTR: Refresh Token도 교체
        String newRefreshToken = jwtProvider.createRefreshToken(userId);
        refreshTokenRepository.delete(storedToken);

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProvider.getRefreshTokenExpSeconds());
        refreshTokenRepository.save(new RefreshToken(newRefreshToken, userId, expiresAt));

        return new TokenResult(new TokenInfo(newAccessToken, expiresIn), newRefreshToken);
    }
}