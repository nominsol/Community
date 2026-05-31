package com.example.Community.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;
    private String content;
    private String image;

    @Column(name = "view_count")
    private int viewCount;

    @CreatedDate
    @Column(name = "reg_dat")
    private LocalDateTime createdDate;  //자동으로 생성일자 입력

    @LastModifiedDate
    @Column(name = "upd_dat")
    private LocalDateTime lastModifiedDate;  //자동으로 수정일자 입력

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    public Post(String title, String content, String image, User author){
        this.title = title;
        this.content = content;
        this.image = image;
        this.author = author;
    }

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeImage(String image) {
        this.image = image;
    }
}
