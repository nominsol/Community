package com.example.Community.controller;

import com.example.Community.dto.UserRequestDto;
import com.example.Community.dto.UserResponseDto;
import com.example.Community.response.ApiResponse;
import com.example.Community.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto request){
        UserResponseDto result = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/users" + result.getId())
                .body(ApiResponse.of("USER_CREATED", result));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUser(@PathVariable Long userId) {
        UserResponseDto result = userService.getUser(userId);
        return ResponseEntity.ok(
                ApiResponse.of("USER_RETRIEVED", result)
        );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserInfo(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequestDto request
    ) {
        UserResponseDto result = userService.updateUserInfo(userId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("USERINFO_UPDATED", result));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("USER_DELETED", null));
    }

}
