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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    //로그인
    @Transactional
    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthorizedException("INVALID_CREDENTIALS"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new AuthorizedException("INVALID_CREDENTIALS");
        }

        String accessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        long expiresIn = jwtProvider.getAccessTokenValidityInMilliseconds();

        // 기존 Refresh Token 삭제 후 새로 발급
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        refreshTokenRepository.deleteByUserId(user.getId());

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProvider.getRefreshTokenExpSeconds());
        refreshTokenRepository.save(
                new RefreshToken(
                        refreshToken,
                        user.getId(),
                        expiresAt
                )
        );

        LoginResponse response = LoginResponse.of(user, accessToken, expiresIn);
        return new LoginResult(response, refreshToken);
    }

    //액세스 토큰 재발급
    @Transactional
    public TokenResult refreshAccessToken(String rawRefreshToken) {
        // DB에서 토큰 조회
        RefreshToken storedToken = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new AuthorizedException("UNAUTHORIZED"));

        // DB 기준 만료 검사
        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new AuthorizedException("UNAUTHORIZED");
        }

        // JWT 서명/만료 검증
        jwtProvider.parse(rawRefreshToken);

        Long userId = storedToken.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthorizedException("UNAUTHORIZED"));

        String newAccessToken = jwtProvider.createAccessToken(userId, user.getEmail(), user.getNickname());
        long expiresIn = jwtProvider.getAccessTokenValidityInMilliseconds();

        // RTR: Refresh Token도 교체
        String newRefreshToken = jwtProvider.createRefreshToken(userId);
        refreshTokenRepository.delete(storedToken);

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtProvider.getRefreshTokenExpSeconds());
        refreshTokenRepository.save(
                new RefreshToken(
                        newRefreshToken,
                        userId,
                        expiresAt
                )
        );

        return new TokenResult(
                new TokenInfo(newAccessToken, expiresIn),
                newRefreshToken
        );
    }

    //로그아웃
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
    }
}