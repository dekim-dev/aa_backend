package dekim.aa_backend.service;

import dekim.aa_backend.dto.GlobalResponseDTO;
import dekim.aa_backend.dto.PostRequestDTO;
import dekim.aa_backend.dto.PostResponseDTO;
import dekim.aa_backend.entity.Post;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.PostRepository;
import dekim.aa_backend.persistence.UserRepository;
import dekim.aa_backend.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
              .title(post.getTitle())
              .content(post.getContent())
              .createdAt(post.getCreatedAt())
              .build();
    } else {
      throw new EntityNotFoundException("Post not found");
    }
  }


  public PostResponseDTO writePost(PostRequestDTO postRequestDTO, User user) {
    // 사용자 엔티티(User)에서 닉네임 가져오기
    User currentUser = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
    String nickname = currentUser.getNickname();

    // Post 엔티티 생성 및 닉네임 및 사용자 정보 설정
    Post post = Post.builder()
            .boardCategory(postRequestDTO.getBoardCategory())
            .title(postRequestDTO.getTitle())
            .content(postRequestDTO.getContent())
            .user(currentUser) // 사용자 정보 설정
            .build();

    postRepository.save(post);
    return PostResponseDTO.builder()
            .boardCategory(post.getBoardCategory())
            .title(post.getTitle())
            .content(post.getContent())
            .nickname(postRequestDTO.getNickname())
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
}


