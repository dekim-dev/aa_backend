package dekim.aa_backend.controller;

import dekim.aa_backend.dto.AdvertisementDTO;
import dekim.aa_backend.dto.UserInfoDTO;
import dekim.aa_backend.entity.Advertisement;
import dekim.aa_backend.entity.Diary;
import dekim.aa_backend.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                // 사용자 정보가 없는 경우 처리
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            UserInfoDTO userInfo = mainService.getUserInfo(Long.valueOf(userDetails.getUsername()));
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("회원정보 조회 실패", HttpStatus.NOT_FOUND);
        }
    }


    // 광고
    @GetMapping("/ads")
    public ResponseEntity<?> getAllAdvertisements(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                // 사용자 정보가 없는 경우 처리
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            List<AdvertisementDTO> advertisementDTOS = mainService.getAllAdvertisements(Long.valueOf(userDetails.getUsername()));
            return new ResponseEntity<>(advertisementDTOS, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("광고 조회 실패", HttpStatus.NOT_FOUND);
        }
    }
}
