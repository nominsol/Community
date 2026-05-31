package com.example.Community.controller;

import com.example.Community.domain.entity.User;
import com.example.Community.dto.UserRequestDto;
import com.example.Community.dto.UserResponseDto;
import com.example.Community.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @Transactional
    public UserResponseDto createUser(@RequestBody UserRequestDto request){
        return userService.createUser(request);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    @Transactional
    public UserResponseDto updateUserInfo(
            @PathVariable Long userId,
            @RequestBody UserRequestDto request
    ) {
        return userService.updateUserInfo(userId, request);
    }

    @DeleteMapping("/{userId}")
    @Transactional
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

}
