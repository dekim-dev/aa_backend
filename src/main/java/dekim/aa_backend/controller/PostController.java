package dekim.aa_backend.controller;

import dekim.aa_backend.dto.PostRequestDTO;
import dekim.aa_backend.dto.PostResponseDTO;
import dekim.aa_backend.entity.Post;
import dekim.aa_backend.security.CustomUserDetails;
import dekim.aa_backend.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/post")
public class PostController {

  @Autowired
  private PostService postService;

  @GetMapping("/{postId}")
  public ResponseEntity<?> retrievePost(@PathVariable Long postId) {
    try {
      PostResponseDTO post = postService.bringPost(postId);
      return ResponseEntity.ok().body(post);
    } catch (Exception e) {
      log.warn("Error retrieving post with ID: " + postId, e);
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/")
  public ResponseEntity<PostResponseDTO> createPost(
          @AuthenticationPrincipal CustomUserDetails user,
          @RequestBody PostRequestDTO dto
  ) {
    try {
      // 현재 로그인한 사용자의 정보(user)를 writePost 메서드에 전달하여 게시글 작성
      PostResponseDTO post = postService.writePost(dto, user.getUser());
      return ResponseEntity.ok(post);
    } catch (Exception e) {
      log.warn("Error creating post: ", e);
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/category/{boardCategory}")
  public ResponseEntity<Page<PostResponseDTO>> getPostsByBoardCategory(
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int pageSize,
          @PathVariable String boardCategory
  ) {
    try {
      Page<PostResponseDTO> postResponsePage = postService.fetchPostsByBoardCategory(page, pageSize, boardCategory);
      log.info("🎈성공: " + postResponsePage);
      return ResponseEntity.ok(postResponsePage);
    } catch (Exception e) {
      log.warn("🧨에러: " + e);
      return ResponseEntity.badRequest().build();
    }
  }

}
