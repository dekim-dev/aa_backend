package dekim.aa_backend.controller;

import dekim.aa_backend.dto.*;
import dekim.aa_backend.exception.DuplicatePostReportException;
import dekim.aa_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/posts")
    public ResponseEntity<?> getUserPosts(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        Page<PostResponseDTO> postResponseDTOS = userService.getUserPost(Long.valueOf(userDetails.getUsername()), page, pageSize);
        return new ResponseEntity<>(postResponseDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/posts")
    public ResponseEntity<String> deleteMultiplePosts(@AuthenticationPrincipal UserDetails userDetails, @RequestBody List<Long> postIds) {
        try {
            userService.deleteMultiplePosts(Long.valueOf(userDetails.getUsername()), postIds);
            return ResponseEntity.ok("Posts deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete posts: " + e.getMessage());
        }
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getUserComments(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int pageSize) {
        if (userDetails == null) { // 사용자 정보가 없는 경우 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        Page<CommentDTO> commentDTOS = userService.getUserComment(Long.valueOf(userDetails.getUsername()), page, pageSize);
        return new ResponseEntity<>(commentDTOS, HttpStatus.OK);
    }

    @DeleteMapping("/comments")
    public ResponseEntity<?> deleteMultipleComments(@AuthenticationPrincipal UserDetails userDetails, @RequestBody List<Long> commentIds) {
        try {
            userService.deleteMultipleComments(Long.valueOf(userDetails.getUsername()), commentIds);
            return new ResponseEntity<>("Comment Id : " + commentIds + " were deleted.", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete comments: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        UserInfoAllDTO userInfoAllDTO = userService.getUserInfo(Long.valueOf(userDetails.getUsername()));
        return new ResponseEntity<>(userInfoAllDTO, HttpStatus.OK);
    }

    @PutMapping("/nickname")
    public ResponseEntity<?> updateUserNickname(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> requestBody) {
        try {
            String newNickname = requestBody.get("newNickname");
            UserInfoAllDTO updatedUserInfo = userService.updateUserNickname(
                    Long.valueOf(userDetails.getUsername()), newNickname);
            return new ResponseEntity<>(updatedUserInfo, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> updateUserPassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> requestBody) {
        try {
            String newPwd = requestBody.get("newPwd");
            String conNewPwd = requestBody.get("conNewPwd");
            UserInfoAllDTO updatedUserInfo = userService.updateUserPwd(
                    Long.valueOf(userDetails.getUsername()), newPwd, conNewPwd);
            return new ResponseEntity<>(updatedUserInfo, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/pfImg")
    public ResponseEntity<UserInfoAllDTO> updateUserPfImg(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> requestBody) {
        String newPfImg = requestBody.get("newPfImg");
        UserInfoAllDTO updatedUserInfo = userService.updateUserPfImg(
                Long.valueOf(userDetails.getUsername()), newPfImg);
        return new ResponseEntity<>(updatedUserInfo, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(Long.valueOf(userDetails.getUsername()));
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

    // 회원 차단
    @PostMapping("/block/{blockedUserId}")
    public ResponseEntity<?> blockUser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long blockedUserId) {
        try {
            userService.blockUser(Long.valueOf(userDetails.getUsername()), blockedUserId);
            return new ResponseEntity<>("회원 차단 완료", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    // 회원 차단 해제
    @DeleteMapping("/block/{blockedUserId}")
    public ResponseEntity<?> unblockUser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long blockedUserId) {
        try {
            userService.unblockUser(Long.valueOf(userDetails.getUsername()), blockedUserId);
            return new ResponseEntity<>("회원 차단해제 완료", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

    }

    // 회원 신고
    @PostMapping("/report")
    public ResponseEntity<?> reportUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ReportRequestDTO reportRequestDTO) {
        try {
            userService.reportUser(Long.valueOf(userDetails.getUsername()), reportRequestDTO);
            return new ResponseEntity<>("회원 신고 완료", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    // 회원 신고 취소(삭제)
    @DeleteMapping("/report/{reportId}")
    public ResponseEntity<?> cancelReportUser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long reportId) {
        userService.cancelReportUser(Long.valueOf(userDetails.getUsername()), reportId);
        return new ResponseEntity<>("회원 신고 취소 완료", HttpStatus.OK);
    }

    // 게시글 신고/취소
    @PutMapping("/post/{postId}/report")
    public ResponseEntity<?> reportPost(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId) {
        try {
            userService.reportPost(Long.valueOf(userDetails.getUsername()), postId);
            return new ResponseEntity<>("게시글 신고 완료", HttpStatus.OK);
        } catch (DuplicatePostReportException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 문의
    @PostMapping("/inquiry")
    public ResponseEntity<?> createInquiry(@AuthenticationPrincipal UserDetails userDetails, @RequestBody InquiryRequestDTO inquiryRequestDTO) {
        try {
            userService.createInquiry(Long.valueOf(userDetails.getUsername()), inquiryRequestDTO);
            return new ResponseEntity<>(inquiryRequestDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

}