package com.example.Community.service;

import com.example.Community.domain.entity.Post;
import com.example.Community.domain.entity.PostStat;
import com.example.Community.domain.entity.User;
import com.example.Community.domain.repository.*;
import com.example.Community.dto.PostRequestDto;
import com.example.Community.dto.PostResponseDto;
import com.example.Community.exception.AuthorizedException;
import com.example.Community.exception.BusinessException;
import com.example.Community.exception.NotFoundException;
import com.example.Community.util.FileUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final PostStatRepository postStatRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public PostResponseDto createPost(Long userId, PostRequestDto request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                author
        );

        //게시글 먼저 저장(post_id 먼저 발급받으려고)
        Post savedPost = postRepository.save(post);

        // 이미지 경로 처리
        if (request.getImage() != null && !request.getImage().isBlank()) {
            // 이미지가 여러 개인 경우를 고려
            String[] splitImages = request.getImage().split(",");
            for (String imgUrl : splitImages) {
                // 전체 URL인 경우 상대경로로 변환하여 DB 파일 검색
                String relativePath = FileUtil.extractPathFromUrl(imgUrl.trim());
                fileRepository.findByFilePath(relativePath).ifPresent(file -> {
                    file.associatePost(savedPost); // 파일에 생성된 게시글 정보 연결
                });
            }
        }
        return new PostResponseDto(savedPost);
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        PostStat postStat = postStatRepository.findById(postId).orElse(null);

        boolean isLiked = currentUserId != null &&
                postLikeRepository.findByUserIdAndPostId(currentUserId, postId).isPresent();

        return new PostResponseDto(post, postStat, isLiked);
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        post.changeTitle(request.getTitle());
        post.changeContent(request.getContent());

        // 수정 시 기존 매핑된 파일 관계를 초기화하고 재매핑
        post.getImages().forEach(file -> file.associatePost(null));
        post.getImages().clear();

        if (request.getImage() != null && !request.getImage().isBlank()) {
            String[] splitImages = request.getImage().split(",");
            for (String imgUrl : splitImages) {
                String relativePath = FileUtil.extractPathFromUrl(imgUrl.trim());
                fileRepository.findByFilePath(relativePath).ifPresent(file -> {
                    file.associatePost(post);
                });
            }
        }

        return new PostResponseDto(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("POST_NOT_FOUND"));

        postStatRepository.deleteById(postId);
        postLikeRepository.deleteAll(postLikeRepository.findByPostId(postId));
        commentRepository.deleteAll(commentRepository.findByPostId(postId));

        post.getImages().forEach(file -> file.associatePost(null));

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "createdDate"));
        List<Post> posts = postRepository.findAll(pageable).getContent();

        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
        Map<Long, PostStat> statMap = postStatRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(PostStat::getId, stat -> stat));

        return posts.stream()
                .map(post -> new PostResponseDto(post, statMap.get(post.getId()), false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> searchPosts(String keyword, int offset, int limit, String sort) {
        Sort sortObj = sort.equals("recent")
                ? Sort.by(Sort.Direction.DESC, "createdDate")
                : Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(offset / limit, limit, sortObj);
        List<Post> posts = postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable).getContent();

        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
        Map<Long, PostStat> statMap = postStatRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(PostStat::getId, stat -> stat));

        return posts.stream()
                .map(post -> new PostResponseDto(post, statMap.get(post.getId()), false))
                .collect(Collectors.toList());
    }
}
