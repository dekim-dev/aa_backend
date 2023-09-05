package dekim.aa_backend.dto;

import dekim.aa_backend.entity.User;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
  private String email;

  public static UserResponseDTO of(User user) {
    return new UserResponseDTO(user.getEmail());
  }
}

// of : 주로 데이터 변환 또는 매핑을 위해 사용되는 메소드
//      위 코드에서는 User 엔티티를 UserResponseDTO로 변환