package dekim.aa_backend.controller;

import dekim.aa_backend.dto.UserInfoAllDTO;
import dekim.aa_backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  @Autowired
  AdminService adminService;

  @GetMapping("/users")
  public ResponseEntity<List<UserInfoAllDTO>> getAllUsers() {
    List<UserInfoAllDTO> userInfoList = adminService.getAllUserInfo();
    return ResponseEntity.ok(userInfoList);
  }

  @DeleteMapping("/users")
  public ResponseEntity<String> deleteMultipleUsers(@RequestBody List<Long> userIds) {
    adminService.deleteMultipleUsers(userIds);
    return ResponseEntity.ok("Users deleted successfully.");
  }

  @PutMapping("/users")
  public ResponseEntity<String> updateUserInfo(@RequestBody UserInfoAllDTO userInfoAllDTO) {
    adminService.updateUserInfo(userInfoAllDTO);
    return ResponseEntity.ok("User information updated successfully.");
  }
}
