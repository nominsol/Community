package com.example.Community.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.Persistable;

@Entity
@Getter
@Table(name = "post_stats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostStat implements Persistable<Long> {

    @Id
    @Column(name = "post_id")
    private Long id;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "comment_count", nullable = false)
    private Long commentCount;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

    // Persistable 구현을 위한 상태 플래그
    @Transient
    private boolean isNew = true;

    public PostStat(Post post, Long viewCount, Long commentCount, Long likeCount) {
        this.post = post;
        if (post != null) {
            this.id = post.getId();
        }
        this.viewCount = viewCount != null ? viewCount : 0L;
        this.commentCount = commentCount != null ? commentCount : 0L;
        this.likeCount = likeCount != null ? likeCount : 0L;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    // DB 저장(Persist) 후 또는 DB 조회(Load) 후에는 isNew를 false로 변경
    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        this.commentCount--;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }
}