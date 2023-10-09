package dekim.aa_backend.controller;

import dekim.aa_backend.dto.kakaoPay.KakaoApproveResponseDTO;
import dekim.aa_backend.service.KakaoPayService;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;

    // 결제 요청
    @PostMapping("/ready")
    public ResponseEntity<?> readyToKakaoPay(@AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(kakaoPayService.kakaoPayReady(Long.valueOf(userDetails.getUsername())), HttpStatus.OK);
    }

    // 결제 성공
    @GetMapping("/success")
    public ResponseEntity<?> afterPayRequest(@RequestParam("pg_token") String pgToken,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        KakaoApproveResponseDTO kakaoApproveResponse = kakaoPayService.approveResponse(Long.valueOf(userDetails.getUsername()), pgToken);
        return new ResponseEntity<>(kakaoApproveResponse, HttpStatus.OK);
    }

    // 결제 진행 중 취소
    @GetMapping("/cancel")
    public void cancel() {
        throw new IllegalArgumentException("오류로인해 결제가 취소 되었습니다.");
    }

    // 결제 실패
    @GetMapping("/fail")
    public void fail() {
        throw new IllegalArgumentException("결제 실패하였습니다.");
    }
}
