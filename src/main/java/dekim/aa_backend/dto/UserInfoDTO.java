package dekim.aa_backend.dto;

import dekim.aa_backend.constant.Authority;
import dekim.aa_backend.constant.IsPaidMember;
import dekim.aa_backend.entity.User;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

    private Long id;
    private String pfImg;
    private Authority authority;
    private IsPaidMember isPaidMember;
    private String nickname;

    public static UserInfoDTO of(User user) {
        return new UserInfoDTO(user.getId(), user.getPfImg(), user.getAuthority(), user.getIsPaidMember(), user.getNickname());
    }
}

// of : 주로 데이터 변환 또는 매핑을 위해 사용되는 메소드
//      위 코드에서는 User 엔티티를 UserResponseDTO로 변환