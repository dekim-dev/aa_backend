package dekim.aa_backend.controller;

import dekim.aa_backend.dto.AdvertisementDTO;
import dekim.aa_backend.dto.ClinicDTO;
import dekim.aa_backend.dto.UserInfoAllDTO;
import dekim.aa_backend.entity.Advertisement;
import dekim.aa_backend.entity.Clinic;
import dekim.aa_backend.service.AdminService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<Page<UserInfoAllDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int pageSize) {
    Page<UserInfoAllDTO> userInfoList = adminService.getAllUserInfo(PageRequest.of(page, pageSize));
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
    return new ResponseEntity<>(clinic, HttpStatus.OK);
  }

  @PutMapping("/clinic/{clinicId}")
  public ResponseEntity<Clinic> updateClinic(@PathVariable Long clinicId, @RequestBody ClinicDTO clinicDTO) {
    Clinic updatedClinic = adminService.updateClinic(clinicId, clinicDTO);
    return ResponseEntity.ok(updatedClinic);
  }

  @DeleteMapping("/clinic")
  public ResponseEntity<String> deleteClinic(@RequestBody List<Long> clinicIds) {
    adminService.deleteClinic(clinicIds);
    return ResponseEntity.ok("Clinic deleted successfully.");
  }

  // 광고 등록
  @PostMapping("/advertisement")
  public ResponseEntity<?> registerClinic(@RequestBody Advertisement advertisement) {
    try {
      Advertisement registeredAd = adminService.registerAdvertisement(advertisement);
      return ResponseEntity.ok(registeredAd);
    } catch (Exception e) {
      return new ResponseEntity<>("광고 등록 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // 광고 조회 (모든 광고)
  @GetMapping("/advertisement")
  public ResponseEntity<?> getAllAds(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int pageSize) {
    try {
      Page<Advertisement> advertisementList = adminService.getAdvertisement(PageRequest.of(page, pageSize));
      return ResponseEntity.ok(advertisementList);
    } catch (Exception e) {
      return new ResponseEntity<>("광고 조회 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 광고 수정
  @PatchMapping("/advertisement/{id}")
  public ResponseEntity<?> updateAd(@PathVariable Long id, @RequestBody AdvertisementDTO updatedAd) {
    try {
      Advertisement updatedAdvertisement = adminService.updateAdvertisement(id, updatedAd);
      return ResponseEntity.ok(updatedAdvertisement);
    } catch (EntityNotFoundException e) {
      return new ResponseEntity<>("광고 수정 실패: " + e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("광고 수정 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 광고 삭제
  @DeleteMapping("/advertisement")
  public ResponseEntity<?> deleteAd(@RequestBody List<Long> ids) {
    try {
      adminService.deleteAdvertisement(ids);
      return ResponseEntity.ok("광고 삭제 성공");
    } catch (EntityNotFoundException e) {
      return new ResponseEntity<>("광고 삭제 실패: " + e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("광고 삭제 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

}
