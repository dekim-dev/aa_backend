package dekim.aa_backend.controller;

import dekim.aa_backend.dto.LikesDTO;
import dekim.aa_backend.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikesController {

  private final LikesService likesService;

  @PostMapping()
  public ResponseEntity<?> createDeleteALike(@AuthenticationPrincipal UserDetails userDetails, @RequestBody LikesDTO likesDTO) {
    try {
      if (userDetails == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
      }
      LikesDTO likes = likesService.createDeleteALike(Long.valueOf(userDetails.getUsername()), likesDTO);
      if(likes.isAdded()) {
        return new ResponseEntity<>(likes, HttpStatus.CREATED); // 좋아요 추가
      }
      return new ResponseEntity<>(likes, HttpStatus.OK); // 이미 좋아요가 존재하는경우 삭제
    } catch (Exception e) {
      return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
    }
  }
}
