package com.example.Community.config;

import com.example.Community.domain.entity.File;
import com.example.Community.domain.entity.FileCategory;
import com.example.Community.domain.entity.User;
import com.example.Community.domain.repository.FileRepository;
import com.example.Community.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

@Configuration
@Profile("development")
@RequiredArgsConstructor
public class SeedConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileRepository fileRepository;

    @Bean
    ApplicationRunner seedRunner() {
        return arguments -> seed(); // 부트 기동 후 1회 실행
    }

    @Transactional
    void seed() {
        if (userRepository.count() >= 10) return;

        // tester1 ~ tester10 계정 더미 데이터
        IntStream.rangeClosed(1, 10).forEach(i -> {
            String rawPassword = "12341234aS!" + i;

            // 3. File을 먼저 생성하고 DB에 저장(save)하여 영속 상태로 만듭니다.
            File file = new File("/public/images/default.jpg", FileCategory.PROFILE_IMAGE, (long)i);
            File savedFile = fileRepository.save(file);

            // 4. DB에 저장된 savedFile을 User에 넣어줍니다.
            User user = new User("tester" + i + "@adapterz.kr", passwordEncoder.encode(rawPassword), "tester" + i, savedFile);
            userRepository.save(user);
        });
    }
}
