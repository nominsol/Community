# Lulu Community — Back-end

## Back-end 소개

- 회원가입/로그인부터 게시글·댓글·좋아요까지 갖춘 `커뮤니티 서비스`의 백엔드 프로젝트입니다.
- `Spring Boot`(Spring Security, Spring Data JPA)로 REST API 서버를 구현하고, `MySQL(Amazon RDS)`을 데이터베이스로 사용했습니다.
- JWT 기반 인증(Access Token + Refresh Token)을 직접 구현했고, 프로필/게시글 이미지는 `Amazon S3`에 업로드하도록 구성했습니다.
- 단순 애플리케이션 개발에 그치지 않고, **Helm + ArgoCD 기반 GitOps 배포 파이프라인**과 **Kubernetes 클러스터 인프라 설계·운영**까지 직접 진행했습니다.
- Controller-Service-Repository 패턴으로 구현했습니다.

### 개발 인원 및 기간

- 개발기간: 2026-06 ~ 2026-08
- 개발 인원: 백엔드/인프라 1명 (노민솔)

### 사용 기술 및 tools

- Spring Boot, Spring Security, Spring Data JPA (Hibernate)
- MySQL (Amazon RDS)
- JWT (Access Token 5분 / Refresh Token 회전)
- AWS S3 (파일 업로드), AWS SDK for Java
- Docker, Kubernetes (kubeadm 자체 구축 클러스터), Helm, ArgoCD (GitOps)
- NGINX Ingress Controller, cert-manager (Let's Encrypt), AWS NLB, CloudFront
- Prometheus + Grafana (kube-prometheus-stack), Loki + Promtail (모니터링/로그)
- GitHub Actions (CI), Amazon ECR

### Front-end

- <a href="https://github.com/100-hours-a-week/4-lulu-community-FE">Front-end Github</a>


## 서버 설계

### 서버 구조

| | route | controller | service |
|:---|:---|:---|:---|
| 인증 | `/auth/**`, `/token/refresh` | AuthController | AuthService |
| 유저 | `/users/**` | UserController | UserService |
| 게시글 | `/posts` | PostController | PostService |
| 댓글 | `/posts/{postId}/comments` | CommentController | CommentService |
| 좋아요 | `/posts/{postId}/likes` | PostLikeController | PostLikeService |
| 게시글 통계 | (내부 연동) | - | PostStatService |
| 파일 업로드 | `/users/upload/profile-image`, `/posts/upload/attach-file` | FileController | FileService |

### 주요 API

| Method | URL | 설명 |
|:---|:---|:---|
| POST | `/users` | 회원가입 |
| POST | `/auth` | 로그인 (Access Token 발급 + Refresh Token 쿠키 세팅) |
| POST | `/token/refresh` | Refresh Token으로 Access Token 재발급 |
| GET | `/auth/check` | 로그인 세션 확인 |
| POST | `/auth/logout` | 로그아웃 |
| GET/PATCH/DELETE | `/users/{userId}` | 회원 정보 조회/수정/탈퇴 |
| POST | `/users/upload/profile-image` | 프로필 이미지 업로드 (S3) |
| GET | `/posts` | 게시글 목록 조회 (페이지네이션) |
| GET | `/posts/search` | 게시글 검색 |
| POST | `/posts` | 게시글 작성 |
| GET/PATCH/DELETE | `/posts/{postId}` | 게시글 조회/수정/삭제 |
| POST | `/posts/upload/attach-file` | 게시글 첨부 이미지 업로드 (S3) |
| GET/POST | `/posts/{postId}/comments` | 댓글 목록/작성 |
| PUT/DELETE | `/posts/{postId}/comments/{commentId}` | 댓글 수정/삭제 |
| POST/DELETE | `/posts/{postId}/likes` | 게시글 좋아요/좋아요 취소 |

### 구현 기능

- 회원가입 / 로그인 / 로그아웃, JWT Access Token(5분) + Refresh Token(HttpOnly 쿠키) 기반 인증
- 이메일/닉네임 중복 확인, 회원 정보 수정, 회원 탈퇴 (연관 게시글/댓글 함께 정리)
- 게시글 CRUD, 페이지네이션 조회, 제목/내용 기반 검색(최신순/인기순 정렬)
- 댓글 CRUD
- 게시글 좋아요 / 좋아요 취소
- 게시글 조회수·좋아요수·댓글수 통계 관리 (PostStat)
- 프로필 이미지 / 게시글 첨부 이미지 업로드 (S3 저장 후 CloudFront로 서빙)

## 배포/인프라

- **컨테이너화**: 멀티스테이지 Dockerfile(Gradle 빌드 → JRE 실행 이미지)로 이미지 경량화
- **오케스트레이션**: kubeadm으로 직접 구축한 Kubernetes 클러스터(마스터 1대 + 워커 2대)에 Helm Chart로 배포
- **CI/CD (GitOps)**: GitHub Actions가 커밋마다 이미지를 빌드해 ECR에 푸시하고, 별도 GitOps 저장소의 `values.yaml` 이미지 태그를 갱신 → ArgoCD가 변경을 감지해 자동으로 무중단 롤링 업데이트 수행
- **Ingress/TLS**: NGINX Ingress Controller + cert-manager(Let's Encrypt)로 경로 기반 라우팅과 TLS 자동 갱신 구성
- **로드밸런싱**: AWS NLB(L4 TCP 패스스루)로 워커 노드 NodePort까지 트래픽 전달, 라우팅/TLS는 ingress-nginx에 위임
- **모니터링/로그**: kube-prometheus-stack(Prometheus+Grafana)으로 메트릭 수집·대시보드 구성, Loki+Promtail로 파드 로그 중앙 수집

### 서비스 시연 영상

- (제출 시 README에 링크 업데이트 예정)
