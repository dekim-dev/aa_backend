package dekim.aa_backend.service;

import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.UserRepository;
import dekim.aa_backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService { // 사용자 정보를 가져오는 인터페이스
  private final UserRepository userRepository;

  // 사용자 이름(email)로 사용자의 정보를 가져오는 메서드 필수구현!
  @Override
  public UserDetails loadUserByUsername(String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException((email)));
    return new CustomUserDetails(user); // User 정보를 사용하여 CustomUserDetails 객체 생성
  }
}