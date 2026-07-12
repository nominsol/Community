package com.example.Community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CommunityApplication {
	//테스트 주석
	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
