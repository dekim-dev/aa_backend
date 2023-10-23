package dekim.aa_backend.controller;

import dekim.aa_backend.dto.DiaryDTO;
import dekim.aa_backend.entity.Diary;
import dekim.aa_backend.service.DiaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diary")
@Slf4j
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
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> fetchDiaryList(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<DiaryDTO> diaryPage = diaryService.fetchAllDiaries(Long.valueOf(userDetails.getUsername()), page, pageSize);
            return new ResponseEntity<>(diaryPage, HttpStatus.OK);
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

    @GetMapping("/{diaryId}")
    public ResponseEntity<?> fetchDiaryById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long diaryId) {
        try {
            DiaryDTO diaryDTO = diaryService.fetchDiaryById(Long.valueOf(userDetails.getUsername()), diaryId);
            return new ResponseEntity<>(diaryDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Error retrieving diary with ID: " + diaryId, e);
            return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<?> deleteByDiaryId(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long diaryId) {
        try {
            diaryService.deleteDiaryById(Long.valueOf(userDetails.getUsername()), diaryId);
            return new ResponseEntity<>("다이어리 삭제 완료", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/{diaryId}")
    public ResponseEntity<?> updateDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long diaryId,
            @RequestBody DiaryDTO updatedDiary) {
        try {
            diaryService.updateDiary(Long.valueOf(userDetails.getUsername()), diaryId, updatedDiary);
            return new ResponseEntity<>(updatedDiary, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/{diaryId}/{medId}")
    public ResponseEntity<?> deleteByDiaryId(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long diaryId, @PathVariable Long medId) {
        try {
            diaryService.deleteMedicationListById(Long.valueOf(userDetails.getUsername()), medId);
            return new ResponseEntity<>("복용약 리스트 삭제 완료", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/diary")
    public ResponseEntity<String> deleteMultipleDiaries(@AuthenticationPrincipal UserDetails userDetails, @RequestBody List<Long> diaryIds) {
        try {
            diaryService.deleteMultipleDiaries(Long.valueOf(userDetails.getUsername()), diaryIds);
            return ResponseEntity.ok("Diaries deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete diaries: " + e.getMessage());
        }
    }


}
