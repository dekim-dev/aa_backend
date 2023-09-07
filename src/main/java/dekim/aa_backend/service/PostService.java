package dekim.aa_backend.service;

import dekim.aa_backend.dto.PostRequestDTO;
import dekim.aa_backend.dto.PostResponseDTO;
import dekim.aa_backend.entity.Post;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.PostRepository;
import dekim.aa_backend.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class PostService {

  @Autowired
  private PostRepository postRepository;
  @Autowired
  private UserRepository userRepository;

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
            .user(user)
            .build();

    postRepository.save(post);
    return convertToDTO(post);
  }


  private void validate(final PostRequestDTO dto) {
    if(dto == null) {
      log.warn("Entity cannot be null.");
      throw new RuntimeException("Entity cannot be null.");
    }
    if(dto.getNickname() == null) {
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
    return PostResponseDTO.builder()
            .id(post.getId())
            .boardCategory(post.getBoardCategory())
            .topic(post.getTopic())
            .title(post.getTitle())
            .content(post.getContent())
            .imgUrl(post.getImgUrl())
            .viewCount(post.getViewCount())
            .likes(post.getLikes())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .nickname(post.getUser().getNickname())
            .userId(post.getUser().getId())
            .pfImg(post.getUser().getPfImg())
            .comments(post.getComments())
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
  }

