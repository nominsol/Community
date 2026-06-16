package com.example.Community.dto;

import com.example.Community.domain.entity.Post;
import com.example.Community.util.FileUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private List<String> imageUrls;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.authorId = post.getAuthor().getId();

        this.imageUrls = post.getImages().stream()
                .map(file -> FileUtil.toFullUrl(file.getFilePath()))
                .collect(Collectors.toList());
    }
}
