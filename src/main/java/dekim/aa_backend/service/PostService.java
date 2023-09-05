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

      return PostResponseDTO.builder()
              .nickname(user.getNickname())
              .boardCategory(post.getBoardCategory())
              .topic(post.getTopic())
              .title(post.getTitle())
              .content(post.getContent())
              .createdAt(post.getCreatedAt())
              .build();
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
    return PostResponseDTO.builder()
            .id(post.getId())
            .boardCategory(post.getBoardCategory())
            .topic(post.getTopic())
            .title(post.getTitle())
            .content(post.getContent())
            .nickname(post.getUser().getNickname())
            .build();
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
            .comments(post.getComments())
            .build();
  }
}


