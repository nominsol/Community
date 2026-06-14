package com.example.Community.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "post_stats")
@RequiredArgsConstructor
public class PostStat {
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

    public PostStat(Post post, Long viewCount, Long commentCount, Long likeCount) {
        this.post = post;
        if (post != null) {
            this.id = post.getId();
        }
        this.viewCount = viewCount != null ? viewCount : 0L;
        this.commentCount = commentCount != null ? commentCount : 0L;
        this.likeCount = likeCount != null ? likeCount : 0L;
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

