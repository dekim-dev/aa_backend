package dekim.aa_backend.controller;

import dekim.aa_backend.config.jwt.TokenProvider;
import dekim.aa_backend.dto.GlobalResponseDTO;
import dekim.aa_backend.dto.UserRequestDTO;
import dekim.aa_backend.dto.UserResponseDTO;
import dekim.aa_backend.exception.EmailAlreadyExistsException;
import dekim.aa_backend.exception.NicknameAlreadyExistsException;
import dekim.aa_backend.persistence.RefreshTokenRepository;
import dekim.aa_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;

  // 회원가입
  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody UserRequestDTO request) {
    try {
      UserResponseDTO responseDTO = userService.create(request);
      return ResponseEntity.ok(responseDTO);
    } catch (EmailAlreadyExistsException | NicknameAlreadyExistsException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e);
    }
  }

  // 로그인
  @PostMapping("/signin")
  public ResponseEntity<?> login(@RequestBody UserRequestDTO request) {
    try {
      UserResponseDTO user = userService.login(request);
        return ResponseEntity.ok().body(user);
    } catch (IllegalArgumentException e) {
      GlobalResponseDTO responseDTO = GlobalResponseDTO.of("Login failed: " + e.getMessage(), 400);
      return ResponseEntity.badRequest().body(responseDTO);
    }
  }
}
