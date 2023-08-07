package dekim.aa_backend.dto;

import dekim.aa_backend.constant.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO { // 회원가입 성공 시 반환받는 DTO
  private String email;
  private String nickname;
  private Role role;
  private String token;
}
