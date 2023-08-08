package dekim.aa_backend.controller;

import dekim.aa_backend.config.jwt.TokenProvider;
import dekim.aa_backend.dto.TokenDTO;
import dekim.aa_backend.dto.UserRequestDTO;
import dekim.aa_backend.dto.UserResponseDTO;
import dekim.aa_backend.entity.RefreshToken;
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

import java.util.HashMap;
import java.util.Map;


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
      UserResponseDTO responseDTO = userService.login(request);
      TokenDTO tokenDTO = tokenProvider.makeTokens(request);
      RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserEmail(responseDTO.getEmail()).orElse(null);

      if (refreshTokenEntity != null) {
        refreshTokenEntity.updateToken(tokenDTO.getRefreshToken());
      } else {
        refreshTokenEntity = new RefreshToken(responseDTO.getEmail(), tokenDTO.getRefreshToken());
      }
      refreshTokenRepository.save(refreshTokenEntity);
      Map<String, Object> responseData = new HashMap<>();
      responseData.put("user", responseDTO);
      responseData.put("tokens", tokenDTO);

      return ResponseEntity.ok(responseData);

//      return ResponseEntity.ok(responseDTO);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e);
    }
  }

}
