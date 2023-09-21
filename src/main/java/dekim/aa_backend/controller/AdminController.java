package dekim.aa_backend.controller;

import dekim.aa_backend.dto.ClinicDTO;
import dekim.aa_backend.dto.UserInfoAllDTO;
import dekim.aa_backend.entity.Clinic;
import dekim.aa_backend.service.AdminService;
import dekim.aa_backend.service.ClinicService;
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

  @PostMapping("/clinic")
  public ResponseEntity<Clinic> registerClinic(@RequestBody ClinicDTO clinicDTO) {
    Clinic clinic = adminService.registerClinic(clinicDTO);
    return ResponseEntity.ok(clinic);
  }

  @PutMapping("/clinic/{clinicId}")
  public ResponseEntity<Clinic> updateClinic(@PathVariable Long clinicId, @RequestBody ClinicDTO clinicDTO) {
    Clinic updatedClinic = adminService.updateClinic(clinicId, clinicDTO);
    return ResponseEntity.ok(updatedClinic);
  }

  @DeleteMapping("/clinic/{clinicId}")
  public ResponseEntity<String> deleteClinic(@PathVariable Long clinicId) {
    adminService.deleteClinic(clinicId);
    return ResponseEntity.ok("Clinic deleted successfully.");
  }
}
