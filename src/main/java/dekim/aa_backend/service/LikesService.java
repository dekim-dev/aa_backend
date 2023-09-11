package dekim.aa_backend.service;

import dekim.aa_backend.dto.LikesDTO;
import dekim.aa_backend.entity.Likes;
import dekim.aa_backend.entity.Post;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.LikesRepository;
import dekim.aa_backend.persistence.PostRepository;
import dekim.aa_backend.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class LikesService {

  private final LikesRepository likesRepository;
  private final UserRepository userRepository;
  private final PostRepository postRepository;

  @Transactional
  public LikesDTO createDeleteALike(Long userId, LikesDTO likesDTO) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
      throw new RuntimeException("User not found");
    }
    // DTO 에서 게시글번호 받기
    Long postId = likesDTO.getPostId();
    // 사용자와 게시물을 조회
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
    Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
    // 이미 좋아요를 누른 게시글인지 확인
    Optional<Likes> existingLike = likesRepository.findByUserIdAndPostId(userId, postId);
    if (existingLike.isEmpty()) {
      Likes newLike = likesRepository.save(Likes.builder()
              .user(user)
              .post(post)
              .build());
      // 존재하지 않으면 추가하고 isAdded 를 true 로 반환
      return LikesDTO.builder().userId(newLike.getUser().getId()).postId(newLike.getPost().getId()).isAdded(true).build();
    }
    likesRepository.delete(existingLike.get());
    // 존재하면 삭제하고 isAdded 를 false 로 반환
    return LikesDTO.builder().userId(userId).postId(postId).isAdded(false).build();
  }

}
