package dekim.aa_backend.controller;

import dekim.aa_backend.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
      return ResponseEntity.ok("병원 정보가 업데이트 성공");
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("병원 정보 업데이트 실패");
    }
  }
}

