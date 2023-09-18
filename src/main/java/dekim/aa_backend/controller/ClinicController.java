package dekim.aa_backend.controller;

import dekim.aa_backend.dto.ClinicRecommendationDTO;
import dekim.aa_backend.dto.ClinicRequestDTO;
import dekim.aa_backend.dto.ClinicSearchResponseDTO;
import dekim.aa_backend.dto.LikesDTO;
import dekim.aa_backend.entity.Clinic;
import dekim.aa_backend.service.ClinicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/clinics")
public class ClinicController {

  private final ClinicService clinicService;

  @GetMapping("")
  public ResponseEntity<?> CallAPiWithJson() {
    try {
      clinicService.insertClinicDataToDB();
      return ResponseEntity.ok("병원 정보 저장 완료");
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("병원 정보 저장 실패");
      }
    }


  @GetMapping("/update")
  public ResponseEntity<String> updateClinics() {
    try {
      clinicService.updateClinicsFromPublicData();
      return ResponseEntity.ok("병원 정보 업데이트 성공");
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("병원 정보 업데이트 실패");
    }
  }


  /* DB에서 병원 리스트 가져오기 */
  @GetMapping("/list")
  public ResponseEntity<Page<Clinic>> getClinicList(@RequestParam("page") int page,
                                                    @RequestParam("pageSize") int pageSize) {
      // 페이징 된 병원 데이터 가져오기
      Page<Clinic> clinicPage = clinicService.fetchClinicList(page, pageSize);
      return ResponseEntity.ok(clinicPage);
  }


  /* 키워드로 병원 검색 */
  @GetMapping("/search")
  public ResponseEntity<?> searchClinicsByKeyword(
          @RequestParam String keyword,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int pageSize
  ) {
    Page<Clinic> clinics = clinicService.searchClinicsByKeyword(keyword, PageRequest.of(page, pageSize));

    ClinicSearchResponseDTO dto = new ClinicSearchResponseDTO();
    dto.setClinics(clinics.getContent());
    dto.setTotalResults(clinics.getTotalElements());

    return ResponseEntity.ok(dto);
  }


/* 병원 디테일 정보 */
  @GetMapping("/{id}")
  public ResponseEntity<?> getClinicInfoById(@PathVariable Long id) {
    ClinicRequestDTO clinic = clinicService.getClinicInfoById(id);
    return ResponseEntity.ok(clinic);
  }


  /* 주소로 병원 검색 */
  @GetMapping("/searchAddress")
  public ResponseEntity<?> searchClinicsByAddress(
          @RequestParam String address,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int pageSize
  ) {
    Page<Clinic> clinics = clinicService.searchClinicsByAddress(address, PageRequest.of(page, pageSize));

    ClinicSearchResponseDTO dto = new ClinicSearchResponseDTO();
    dto.setClinics(clinics.getContent());
    dto.setTotalResults(clinics.getTotalElements());

    return ResponseEntity.ok(dto);
  }

  /* 병원 추천 */
  @PostMapping("/recommendation/{clinicId}")
  public ResponseEntity<?> createDeleteARecommendation(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long clinicId) {
    try {
      if (userDetails == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
      }
      ClinicRecommendationDTO recommendation = clinicService.createDeleteRecommendation(Long.valueOf(userDetails.getUsername()), clinicId);
      if(recommendation.isRecommended()) {
        return new ResponseEntity<>(recommendation, HttpStatus.CREATED); // 추천 추가
      }
      return new ResponseEntity<>(recommendation, HttpStatus.OK); // 이미 추천을 한 경우 삭제
    } catch (Exception e) {
      return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
    }
  }
}

