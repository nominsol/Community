package com.example.Community.service;

import com.example.Community.domain.entity.User;
import com.example.Community.domain.repository.UserRepository;
import com.example.Community.dto.UserRequestDto;
import com.example.Community.dto.UserResponseDto;
import com.example.Community.exception.NotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponseDto createUser(UserRequestDto request){
        User user = new User(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getImage()
        );
        User savedUser = userRepository.save(user);
        return new UserResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUserInfo(@Positive Long userId, @Valid UserRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        user.changeName(request.getName());
        user.changeImage(request.getImage());
        return new UserResponseDto(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
