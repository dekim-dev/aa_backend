package dekim.aa_backend.controller;

import dekim.aa_backend.entity.Diary;
import dekim.aa_backend.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @PostMapping
    public ResponseEntity<?> createDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Diary diary) {
        try {
            if (userDetails == null) {
                // 사용자 정보가 없는 경우 처리
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            Diary newDiary = diaryService.createDiary(diary, Long.valueOf(userDetails.getUsername()));
            return new ResponseEntity<>(newDiary, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("다이어리 생성 실패", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> fetchDiaryList(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<Diary> diaryList = diaryService.fetchAllDiaries(Long.valueOf(userDetails.getUsername()));
            return new ResponseEntity<>(diaryList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("다이어리 조회 실패", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<?> fetchLatestThreeDiaries(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<Diary> diaryList = diaryService.fetchLatestThreeDiaries(Long.valueOf(userDetails.getUsername()));
            return new ResponseEntity<>(diaryList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("다이어리 조회 실패", HttpStatus.NOT_FOUND);
        }
    }

}
