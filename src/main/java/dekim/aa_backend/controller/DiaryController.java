package dekim.aa_backend.controller;

import dekim.aa_backend.entity.Diary;
import dekim.aa_backend.security.CustomUserDetails;
import dekim.aa_backend.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @PostMapping
    public ResponseEntity<?> createDiary(
            @RequestBody Diary diary) {
        try {
            Diary newDiary = diaryService.createDiary(diary);
            return new ResponseEntity<>(newDiary, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("다이어리 생성 실패", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> fetchDiaryList() {
        try {
            List<Diary> diaryList = diaryService.fetchAllDiaries();
            return new ResponseEntity<>(diaryList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("다이어리 조회 실패", HttpStatus.NOT_FOUND);
        }
    }
}
