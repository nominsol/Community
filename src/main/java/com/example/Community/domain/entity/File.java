package com.example.Community.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "files")
@NoArgsConstructor(access = PROTECTED)
public class File {

    @Column(name="file_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="file_path", nullable = false)
    private String filePath = "/public/images/default.jpg";;

    @Column(name="file_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private FileCategory fileCategory;

    @Column(name="uploader_id")
    private Long uploaderId;  // 업로더 추적용 (nullable)

    @CreatedDate
    @Column(name = "reg_date")
    private LocalDateTime createdDate;  //자동으로 생성일자 입력

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    // Constructor
    public File(String filePath, FileCategory fileCategory, Long uploaderId) {
        this.filePath = filePath;
        this.fileCategory = fileCategory;
        this.uploaderId = uploaderId;
    }

    // Factory Methods
    public static File createProfileImage(String filePath, Long uploaderId) {
        return new File(filePath, FileCategory.PROFILE_IMAGE, uploaderId);
    }

    // 게시글 사진 추가
    public static File createPostAttachment(String filePath, Long uploaderId) {
        return new File(filePath, FileCategory.POST_ATTACHMENT, uploaderId);
    }

    public void associatePost(Post post) {
        this.post = post;
    }
}