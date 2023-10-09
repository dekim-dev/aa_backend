package dekim.aa_backend.service;

import dekim.aa_backend.constant.IsPaidMember;
import dekim.aa_backend.dto.kakaoPay.KakaoApproveResponseDTO;
import dekim.aa_backend.dto.kakaoPay.KakaoReadyResponseDTO;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class KakaoPayService {

    private final UserRepository userRepository;

    @Value("${pay.admin-key}")
    private String kakaoPayAdminKey;
    static final String cid = "TC0ONETIME"; // 가맹점 테스트 코드
    private KakaoReadyResponseDTO kakaoReadyResponseDTO;

    public KakaoReadyResponseDTO kakaoPayReady(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 결제 준비를 위한 카카오페이 요청 양식
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("partner_order_id", "가맹점 주문 번호");
        parameters.add("partner_user_id", "가맹점 회원 ID");
        parameters.add("item_name", "멤버십");
        parameters.add("quantity", 1);
        parameters.add("total_amount", 4500);
        parameters.add("tax_free_amount", 500);
        parameters.add("approval_url", "http://localhost:3000/kakao/auth/callback"); // 성공 시 redirect url
        parameters.add("cancel_url", "http://localhost:3000/membership/cancel"); // 취소 시 redirect url
        parameters.add("fail_url", "http://localhost:3000/membership/fail"); // 실패 시 redirect url

        // 파라미터, 헤더
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        // 카카오페이 결제 준비 요청 보내기
        kakaoReadyResponseDTO = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/ready",
                requestEntity,
                KakaoReadyResponseDTO.class);

        return kakaoReadyResponseDTO;
    }

    // 결제 완료 승인
    public KakaoApproveResponseDTO approveResponse(Long userId, String pgToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 결제 승인 요청 파라미터 설정
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", kakaoReadyResponseDTO.getTid());
        parameters.add("partner_order_id", "가맹점 주문 번호");
        parameters.add("partner_user_id", "가맹점 회원 ID");
        parameters.add("pg_token", pgToken);

        // 파라미터, 헤더 설정
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        // 카카오페이 결제 승인 요청 보내기
        KakaoApproveResponseDTO kakaoApproveResponseDTO = restTemplate.postForObject("https://kapi.kakao.com/v1/payment/approve",
                requestEntity, KakaoApproveResponseDTO.class);

        // 결제가 성공하면 사용자 정보 업데이트
        if(kakaoApproveResponseDTO != null) {
            user.setIsPaidMember(IsPaidMember.PAID);
            userRepository.save(user);
        }
        return kakaoApproveResponseDTO;
    }

    // 카카오가 요구하는 헤더값
    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "KakaoAK " + kakaoPayAdminKey;

        httpHeaders.set("Authorization", auth);
        httpHeaders.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return httpHeaders;
    }
}

