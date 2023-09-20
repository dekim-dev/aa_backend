package dekim.aa_backend.controller;

import dekim.aa_backend.dto.CommentDTO;
import dekim.aa_backend.dto.PostResponseDTO;
import dekim.aa_backend.dto.UserInfoAllDTO;
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
    public ResponseEntity<UserInfoAllDTO> updateUserInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> requestBody) {
        String newNickname = requestBody.get("newNickname");
        UserInfoAllDTO updatedUserInfo = userService.updateUserNickname(
                Long.valueOf(userDetails.getUsername()), newNickname);
        return new ResponseEntity<>(updatedUserInfo, HttpStatus.OK);
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
}
