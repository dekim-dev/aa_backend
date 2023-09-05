package dekim.aa_backend.controller;

import dekim.aa_backend.dto.TokenDTO;
import dekim.aa_backend.dto.TokenRequestDTO;
import dekim.aa_backend.dto.UserRequestDTO;
import dekim.aa_backend.dto.UserResponseDTO;
import dekim.aa_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(authService.signup(userRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(authService.login(userRequestDTO));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDTO> reissue(@RequestBody TokenRequestDTO tokenRequestDTO) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");
        log.info("🔑로그아웃용 refreshToken : " + refreshToken);
        try {
            authService.logout(refreshToken);
            return ResponseEntity.ok("로그아웃되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그아웃에 실패했습니다: " + e.getMessage());
        }
    }

}
