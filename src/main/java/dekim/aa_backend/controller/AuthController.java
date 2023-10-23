package dekim.aa_backend.controller;

import dekim.aa_backend.dto.TokenDTO;
import dekim.aa_backend.dto.TokenRequestDTO;
import dekim.aa_backend.dto.UserRequestDTO;
import dekim.aa_backend.dto.UserResponseDTO;
import dekim.aa_backend.service.AuthService;
import dekim.aa_backend.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signup(@RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(authService.signup(userRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            TokenDTO loginUser = authService.login(userRequestDTO);
            return new ResponseEntity<>(loginUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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

    // 닉네임 중복 확인
    @GetMapping("/nickname")
    public boolean isNicknameExists(@RequestParam String nickname) {
        return authService.isNicknameExists(nickname);
    }

    // 이메일 중복 확인
    @GetMapping("/email")
    public boolean isEmailExists(@RequestParam String email) {
        return authService.isEmailExists(email);
    }


    // 회원가입 - 이메일 인증 (인증키 확인)
    @PostMapping("/email_auth")
    public ResponseEntity<Boolean> checkMailWithAuthKey(@RequestParam("email") String email, @RequestParam("authKey") String authKey) throws Exception {
        try {
            authService.checkEmailWithAuthKey(email, authKey);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 임시 비밀번호 발급 & 비밀번호 업데이트
    @GetMapping("/password/{email}")
    public ResponseEntity<?> sendTempPwd(@PathVariable String email) throws Exception {
        try {
            authService.updatePasswordWithAuthKey(email);
            return new ResponseEntity<>("임시 비밀번호 발급&전송 완료", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("가입되어있지 않은 이메일 주소 입니다.", HttpStatus.NOT_FOUND);
        }

    }

}
