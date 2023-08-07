package dekim.aa_backend.dto;

import dekim.aa_backend.constant.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO { // 회원가입 요청시에 사용하는 DTO
  private String email;
  private String password;
  private String nickname;
  private Role role;
}

