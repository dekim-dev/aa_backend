package dekim.aa_backend.service;

import dekim.aa_backend.dto.CommentDTO;
import dekim.aa_backend.dto.PostRequestDTO;
import dekim.aa_backend.dto.PostResponseDTO;
import dekim.aa_backend.entity.Comment;
import dekim.aa_backend.entity.Post;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.CommentRepository;
import dekim.aa_backend.persistence.PostRepository;
import dekim.aa_backend.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

  @Autowired
  private PostRepository postRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CommentRepository commentRepository;

  public Optional<Post> retrieve(Long postId) {
    return postRepository.findById(postId);
  }

  public PostResponseDTO bringPost(Long postId) {
    Optional<Post> postOptional = postRepository.findById(postId);

    if (postOptional.isPresent()) {
      Post post = postOptional.get();
      Long userNo = post.getUser().getId();

      User user = userRepository.findById(userNo)
              .orElseThrow(() -> new EntityNotFoundException("User not found"));

      return convertToDTO(post);

    } else {
      throw new EntityNotFoundException("Post not found");
    }
  }


  public PostResponseDTO writePost(PostRequestDTO postRequestDTO, Long userId) {

    // 1. 현재 인증 정보 가져오기
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    log.info("authenticatioin: " + authentication);

    // 2. 사용자 아이디를 통해 사용자 정보 조회
    Optional<User> userOptional = userRepository.findById(userId);
    if (!userOptional.isPresent()) {
      throw new RuntimeException("User not found");
    }

    User user = userOptional.get();

    // Post 엔티티 생성 및 닉네임 및 사용자 정보 설정
    Post post = Post.builder()
            .boardCategory(postRequestDTO.getBoardCategory())
            .topic(postRequestDTO.getTopic())
            .title(postRequestDTO.getTitle())
            .content(postRequestDTO.getContent())
            .likes(new HashSet<>()) // HashSet 초기화 -> null 에러 해결
            .user(user)
            .build();

    postRepository.save(post);
    return convertToDTO(post);
  }


  private void validate(final PostRequestDTO dto) {
    if (dto == null) {
      log.warn("Entity cannot be null.");
      throw new RuntimeException("Entity cannot be null.");
    }
    if (dto.getNickname() == null) {
      log.warn("Unknown user.");
      throw new RuntimeException("Unknown user.");
    }
  }

  public Page<PostResponseDTO> fetchPostsByBoardCategory(int page, int pageSize, String boardCategory) {
    PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<Post> postPage = postRepository.findByBoardCategory(boardCategory, pageRequest);
    return postPage.map(this::convertToDTO);
  }

  private PostResponseDTO convertToDTO(Post post) {
    // 댓글에서 유저의 정보를 사용하기 위해 commentDTO 사용 -> @JsonIgnore..
    List<CommentDTO> commentDTOList;
    if (post.getComments() != null && !post.getComments().isEmpty()) {
      commentDTOList = post.getComments().stream()
              .map(comment -> CommentDTO.builder()
                      .id(comment.getId())
                      .nickname(comment.getUser().getNickname())
                      .pfImg(comment.getUser().getPfImg())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .updatedAt(comment.getUpdatedAt())
                      .userId(comment.getUser().getId())
                      .build())
              .toList();
    } else {
      commentDTOList = Collections.emptyList();  // 댓글이 없을경우 emptyList 로 성정
    }
    return PostResponseDTO.builder()
            .id(post.getId())
            .boardCategory(post.getBoardCategory())
            .topic(post.getTopic())
            .title(post.getTitle())
            .content(post.getContent())
            .imgUrl(post.getImgUrl())
            .viewCount(post.getViewCount())
            .likesCount(post.getLikes().size())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .nickname(post.getUser().getNickname())
            .userId(post.getUser().getId())
            .pfImg(post.getUser().getPfImg())
            .commentsDTO(commentDTOList) // 댓글 정보를 CommentDTO의 리스트로 설정
            .likes(post.getLikes())
            .reportCount(post.getReportCount())
            .build();
  }

  public PostResponseDTO updatePostById(PostRequestDTO postRequestDTO, Long userId) {
    // 사용자 확인
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 게시물 확인
    Post post = postRepository.findById(postRequestDTO.getId())
            .orElseThrow(() -> new RuntimeException("Post not found"));

    // 사용자의 권한 확인 (예: 사용자 ID를 사용)
    if (!user.getId().equals(post.getUser().getId())) {
      throw new RuntimeException("해당 게시글의 작성자가 아님");
    }

    // 게시물 업데이트
    post.setBoardCategory(postRequestDTO.getBoardCategory());
    post.setTopic(postRequestDTO.getTopic());
    post.setTitle(postRequestDTO.getTitle());
    post.setContent(postRequestDTO.getContent());
    post.setUpdatedAt(postRequestDTO.getUpdatedAt());

    // 게시물 저장
    postRepository.save(post);

    // 업데이트된 게시물 정보 반환
    return convertToDTO(post);
  }

  public void deletePostById(Long postId, Long userId) {
    // 사용자 확인
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // 게시물 확인
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

    // 사용자의 권한 확인 (예: 사용자 ID를 사용)
    if (!user.getId().equals(post.getUser().getId())) {
      throw new RuntimeException("해당 게시글의 작성자가 아님");
    }
    postRepository.deleteById(postId);
  }

  public void increaseViewCount(Long postId, Long userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (!userOptional.isPresent()) {
      throw new RuntimeException("User not found");
    }

    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

    if (!userOptional.get().getId().equals(post.getUser().getId())) {
      post.setViewCount(post.getViewCount() + 1);
      postRepository.save(post);
    } else {
      throw new RuntimeException("Permission denied");
    }
  }

  // 댓글 작성
  public Comment createComment(Long userId, Long postId, CommentDTO commentDTO) throws IllegalAccessException {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalAccessException("Post not Found." + postId));

    return  commentRepository.save(Comment.builder()
                    .id(commentDTO.getId())
            .createdAt(LocalDateTime.now())
            .content(commentDTO.getContent())
            .user(user)
            .post(post)
            .build());
  }

  // 댓글 수정
  public CommentDTO updateComment(Long commentId, CommentDTO commentDTO, Long userId) throws IllegalAccessException {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found."));

    if (user.getId().equals(comment.getUser().getId())) {
      comment.setContent(commentDTO.getContent());
      comment.setUpdatedAt(LocalDateTime.now());
      commentRepository.save(comment);
      return CommentDTO.builder().id(comment.getId()).content(comment.getContent()).updatedAt(comment.getUpdatedAt()).nickname(comment.getUser().getNickname()).userId(comment.getUser().getId()).postId(comment.getPost().getId()).build();
    } else {
      throw new IllegalArgumentException("Unauthorized : not your comment");
    }
  }

  // 댓글 삭제
  public void deleteComment(Long commentId, Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found."));

    if (user.getId().equals(comment.getUser().getId())) {
      commentRepository.delete(comment);
    } else {
      throw new IllegalArgumentException("Unauthorized : not your comment");
    }
  }

  // 제목+본문+게시판이름과 일치하는 검색어로 게시글 검색
  public Page<PostResponseDTO> searchPostsByBoard(Long userId, String keyword, String boardCategory, int page, int pageSize) {
    PageRequest pageRequest = PageRequest.of(page, pageSize);
    return postRepository.searchByTitleOrContentAndBoard(keyword, boardCategory, pageRequest)
            .map(this::convertToDTO);
  }

  // 추천수 10이상인 게시글 -> 베스트 게시판으로
  public Page<PostResponseDTO> getPopularPosts(Long userId, int page, int pageSize) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    PageRequest pageRequest = PageRequest.of(page, pageSize);

    return postRepository.findByLikesCountGreaterThanEqual(10,pageRequest)
            .map(this::convertToDTO);
  }
}
