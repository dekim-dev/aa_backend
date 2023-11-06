package dekim.aa_backend.controller;

import dekim.aa_backend.dto.*;
import dekim.aa_backend.entity.*;
import dekim.aa_backend.service.AdminService;
import dekim.aa_backend.service.ClinicService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {

  @Autowired
  AdminService adminService;
  @Autowired
  ClinicService clinicService;

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

  // 병원 정보 업데이트 from public api
  @GetMapping("/clinic/update")
  public ResponseEntity<String> updateClinics() {
    try {
      clinicService.updateClinicsFromPublicData();
      return ResponseEntity.ok("병원 정보 업데이트 성공");
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("병원 정보 업데이트 실패");
    }
  }
  // ❗️db 관리 담당자용!
  @GetMapping("/clinic/db_manager")
  public ResponseEntity<?> CallAPiWithJson() {
    try {
      clinicService.insertClinicDataToDB();
      return ResponseEntity.ok("병원 정보 저장 완료");
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("병원 정보 저장 실패");
    }
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

  // 게시글 조회 (모든 게시글)
  @GetMapping("/post")
  public ResponseEntity<?> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int pageSize) {
    try {
      Page<PostResponseDTO> postPage = adminService.getAllPosts(PageRequest.of(page, pageSize));
      log.info("🟢postPage: " + postPage);
      return ResponseEntity.ok(postPage);
    } catch (Exception e) {
      return new ResponseEntity<>("게시글 조회 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 게시글 삭제
  @DeleteMapping("/post")
  public ResponseEntity<?> deletePosts(@RequestBody List<Long> ids) {
    try {
      adminService.deletePosts(ids);
      return ResponseEntity.ok("게시글 삭제 성공");
    } catch (EntityNotFoundException e) {
      return new ResponseEntity<>("존재하지 않는 게시글: " + e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("게시글 삭제 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 댓글 조회 (모든 댓글)
  @GetMapping("/comment")
  public ResponseEntity<?> getAllComments(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int pageSize) {
    try {
      Page<CommentDTO> commentPage = adminService.getAllComments(PageRequest.of(page, pageSize));
      return ResponseEntity.ok(commentPage);
    } catch (Exception e) {
      return new ResponseEntity<>("댓글 조회 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 댓글 삭제
  @DeleteMapping("/comment")
  public ResponseEntity<?> deleteComments(@RequestBody List<Long> ids) {
    try {
      adminService.deleteComments(ids);
      return ResponseEntity.ok("댓글 삭제 성공");
    } catch (EntityNotFoundException e) {
      return new ResponseEntity<>("존재하지 않는 댓글: " + e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("댓글 삭제 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 신고 조회
  @GetMapping("/report")
  public ResponseEntity<?> getAllReports(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int pageSize) {
    try {
      Page<ReportResponseDTO> reportPage = adminService.getAllReports(PageRequest.of(page, pageSize));
      return ResponseEntity.ok(reportPage);
    } catch (Exception e) {
      return new ResponseEntity<>("신고 조회 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 신고 삭제
  @DeleteMapping("/report")
  public ResponseEntity<?> deleteReports(@RequestBody List<Long> ids) {
    try {
      adminService.deleteReports(ids);
      return ResponseEntity.ok("신고글 삭제 성공");
    } catch (EntityNotFoundException e) {
      return new ResponseEntity<>("존재하지 않는 신고글: " + e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("신고글 삭제 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 신고 처리
  @PatchMapping("/report/{reportId}")
  public ResponseEntity<?> updateReportStatus(@PathVariable Long reportId) {
    try {
      ReportResponseDTO reportResponseDTO = adminService.updateReportStatus(reportId);
      return ResponseEntity.ok(reportResponseDTO);
    } catch (EntityNotFoundException e) {
      return new ResponseEntity<>("신고내역 존재하지 않음: " + e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("신고상태 수정 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 문의 조회
  @GetMapping("/inquiry")
  public ResponseEntity<?> getAllInquiries(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int pageSize) {
    try {
      Page<InquiryRequestDTO> inquiryRequestDTOPage = adminService.getAllInquiries(PageRequest.of(page, pageSize));
      return new ResponseEntity<>(inquiryRequestDTOPage, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("신고 조회 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  // 문의 처리
  @PatchMapping("/inquiry/{inquiryId}")
  public ResponseEntity<?> updateInquiryStatus(@PathVariable Long inquiryId) {
    try {
      InquiryRequestDTO inquiryRequestDTO = adminService.updateInquiryStatus(inquiryId);
      return new ResponseEntity<>(inquiryRequestDTO, HttpStatus.OK);
    } catch (EntityNotFoundException e) {
      return new ResponseEntity<>("문의내역 존재하지 않음: " + e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      return new ResponseEntity<>("문의상태 수정 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}
