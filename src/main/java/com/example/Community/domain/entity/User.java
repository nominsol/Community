package com.example.Community.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(name="user_email")
    private String email;

    @Column(name="user_pwd")
    private String password;

    @Column(name="user_name")
    private String name;

    @CreatedDate
    @Column(name = "reg_date")
    private LocalDateTime createdDate;  //자동으로 생성일자 입력

    @LastModifiedDate
    @Column(name = "upd_date")
    private LocalDateTime lastModifiedDate;  //자동으로 수정일자 입력

    @OneToMany(mappedBy = "author")
    List<Post> posts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File profileImage;


    public User(String email, String password, String name, File profileImage){
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileImage = profileImage;
    }

    public void changePassword(String password){
        this.password = password;
    }

    public void changeName(String name){
        this.name = name;
    }

    public void updateProfileImage(File profileImage) {
        this.profileImage = profileImage;
    }
}

