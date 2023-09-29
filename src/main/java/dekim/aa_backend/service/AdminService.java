package dekim.aa_backend.service;

import dekim.aa_backend.dto.AdvertisementDTO;
import dekim.aa_backend.dto.ClinicDTO;
import dekim.aa_backend.dto.UserInfoAllDTO;
import dekim.aa_backend.entity.Advertisement;
import dekim.aa_backend.entity.Clinic;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.AdvertisementRepository;
import dekim.aa_backend.persistence.ClinicRepository;
import dekim.aa_backend.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

  private final UserRepository userRepository;
  private final ClinicRepository clinicRepository;
  private final AdvertisementRepository advertisementRepository;

  // 모든 사용자 정보 조회
  @PreAuthorize("hasRole('ADMIN')")
  public List<UserInfoAllDTO> getAllUserInfo() {
    List<User> userList = userRepository.findAll();
    // 관리자를 제외한 사용자 정보만 반환
    return userList.stream()
            .filter(user -> !user.getAuthority().name().contains("ADMIN"))
            .map(this::convertToUserInfoAllDTO)
            .collect(Collectors.toList());
  }

  private UserInfoAllDTO convertToUserInfoAllDTO(User user) {
    return UserInfoAllDTO.builder()
            .id(user.getId())
            .pfImg(user.getPfImg())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .regDate(user.getRegDate())
            .isPaidMember(user.getIsPaidMember())
            .build();
  }

  // 회원 삭제
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteMultipleUsers(List<Long> userIds) {
    for (Long userId : userIds) {
      userRepository.deleteById(userId);
    }
  }

  // 회원 정보 수정
  @PreAuthorize("hasRole('ADMIN')")
  public void updateUserInfo(UserInfoAllDTO userInfoAllDTO) {
    Optional<User> optionalUser = userRepository.findById(userInfoAllDTO.getId());
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      if (userInfoAllDTO.getNickname() != null) {
        user.setNickname(userInfoAllDTO.getNickname());
      }
      if (userInfoAllDTO.getEmail() != null) {
        user.setEmail(userInfoAllDTO.getEmail());
      }
      if (userInfoAllDTO.getIsPaidMember() != null) {
        user.setIsPaidMember(userInfoAllDTO.getIsPaidMember());
      }
      userRepository.save(user);
    } else {
      throw new EntityNotFoundException("User not found with ID: " + userInfoAllDTO.getId());
    }
  }

  // 병원 등록
  @PreAuthorize("hasRole('ADMIN')")
  public Clinic registerClinic(ClinicDTO clinicDTO) {
    Clinic clinic = convertToClinic(clinicDTO);
    return clinicRepository.save(clinic);
  }

  // 병원 정보 수정
  @PreAuthorize("hasRole('ADMIN')")
  public Clinic updateClinic(Long clinicId, ClinicDTO clinicDTO) {
    Clinic existingClinic = clinicRepository.findById(clinicId)
            .orElseThrow(() -> new EntityNotFoundException("Clinic not found with ID: " + clinicId));
    existingClinic.setName(clinicDTO.getName());
    existingClinic.setAddress(clinicDTO.getAddress());
    existingClinic.setHpid(clinicDTO.getHpid());
    existingClinic.setDetailedAddr(clinicDTO.getDetailedAddr());
    existingClinic.setTel(clinicDTO.getTel());
    existingClinic.setInfo(clinicDTO.getInfo());
    existingClinic.setLatitude(clinicDTO.getLatitude());
    existingClinic.setLongitude(clinicDTO.getLongitude());
    existingClinic.setViewCount(clinicDTO.getViewCount());
    existingClinic.setScheduleJson(clinicDTO.getScheduleJson());
    return clinicRepository.save(existingClinic);
  }

  // 병원 삭제
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteClinic(Long clinicId) {
    clinicRepository.deleteById(clinicId);
  }

  private Clinic convertToClinic(ClinicDTO clinicDTO) {
    return Clinic.builder()
            .hpid(clinicDTO.getHpid())
            .name(clinicDTO.getName())
            .address(clinicDTO.getAddress())
            .detailedAddr(clinicDTO.getDetailedAddr())
            .tel(clinicDTO.getTel())
            .info(clinicDTO.getInfo())
            .latitude(clinicDTO.getLatitude())
            .longitude(clinicDTO.getLongitude())
            .scheduleJson(clinicDTO.getScheduleJson())
            .build();
  }

  // 광고 등록
  @PreAuthorize("hasRole('ADMIN')")
  public Advertisement registerAdvertisement(Advertisement advertisement) {
    return advertisementRepository.save(advertisement);
  }

  // 광고 조회 (모든 광고)
  @PreAuthorize("hasRole('ADMIN')")
  public List<Advertisement> getAdvertisement() {
    return advertisementRepository.findAll();
  }

  // 광고 수정
  @PreAuthorize("hasRole('ADMIN')")
  public Advertisement updateAdvertisement(Long id, AdvertisementDTO advertisementDTO) {

    Advertisement updatedAd = advertisementRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Ad Id" + id + "was not found"));

    updatedAd.setAdvertiser(advertisementDTO.getAdvertiser());
    updatedAd.setImgUrl(advertisementDTO.getImgUrl());
    updatedAd.setExpiresOn(advertisementDTO.getExpiresOn());
    return advertisementRepository.save(updatedAd);
  }

  // 광고 삭제
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteAdvertisement(Long id) {
    advertisementRepository.deleteById(id);
  }


}
