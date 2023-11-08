package dekim.aa_backend.controller;

import dekim.aa_backend.dto.AdvertisementDTO;
import dekim.aa_backend.dto.PostResponseDTO;
import dekim.aa_backend.dto.UserInfoDTO;
import dekim.aa_backend.entity.Advertisement;
import dekim.aa_backend.entity.Diary;
import dekim.aa_backend.service.MainService;
import dekim.aa_backend.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;
    private final PostService postService;

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

    // 메인용 최근 게시글 5개 불러오기
    @GetMapping("/post/{boardCategory}")
    public ResponseEntity<?> getFiveLatestPostsForMain(@PathVariable String boardCategory) {
        try {
            List<PostResponseDTO> postResponseDTOList = mainService.fetchTop5PostsFromBoard(boardCategory);
            return new ResponseEntity<>(postResponseDTOList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("게시글 불러오기 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 공지사항 게시글 가져오기
    @GetMapping("/{boardCategory}/{postId}")
    public ResponseEntity<?> getNotice(@PathVariable Long postId, @PathVariable String boardCategory) {
        try {
            PostResponseDTO post = postService.retrieve(postId, boardCategory);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 공지사항 리스트 가져오기
    @GetMapping("/board/notice")
    public ResponseEntity<Page<PostResponseDTO>> getPostsByBoardCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Page<PostResponseDTO> postResponsePage = postService.retrieveFromNoticeBoard(page, pageSize);
            log.info("🎈성공: " + postResponsePage);
            return ResponseEntity.ok(postResponsePage);
        } catch (Exception e) {
            log.warn("🧨에러: " + e);
            return ResponseEntity.badRequest().build();
        }
    }
}
