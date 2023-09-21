package dekim.aa_backend.service;

import dekim.aa_backend.dto.UserInfoAllDTO;
import dekim.aa_backend.entity.User;
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
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteMultipleUsers(List<Long> userIds) {
    for (Long userId : userIds) {
      userRepository.deleteById(userId);
    }
  }

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
}
