package com.example.Community.controller;

import com.example.Community.dto.*;
import com.example.Community.response.ApiResponse;
import com.example.Community.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<SignupResponse>> createUser(
            @Valid @RequestBody SignupRequest signupRequest
    ) {
        SignupResponse result = userService.createUser(signupRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("USER_CREATED", result));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUser(
            @AuthenticationPrincipal Long userId
    ) {
        UserInfoResponse result = userService.getUser(userId);
        return ResponseEntity.ok(
                ApiResponse.of("USER_RETRIEVED", result)
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateName(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        userService.updateName(userId, updateUserRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("NICKNAME_UPDATED", null));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("USER_DELETED", null));
    }

}
