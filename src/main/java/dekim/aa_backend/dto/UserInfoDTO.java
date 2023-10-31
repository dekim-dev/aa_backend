package dekim.aa_backend.dto;

import dekim.aa_backend.constant.Authority;
import dekim.aa_backend.constant.IsPaidMember;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.entity.UserBlock;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private Long id;
    private String pfImg;
    private Authority authority;
    private IsPaidMember isPaidMember;
    private String nickname;
    private List<String> blockedUserIds;

    public static UserInfoDTO of(User user) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.id = user.getId();
        userInfoDTO.pfImg = user.getPfImg();
        userInfoDTO.authority = user.getAuthority();
        userInfoDTO.isPaidMember = user.getIsPaidMember();
        userInfoDTO.nickname = user.getNickname();
        userInfoDTO.blockedUserIds = user.getBlockedUsers().stream()
                .map(UserBlock::getBlockedUser)
                .map(User::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());
        return userInfoDTO;
    }
}


// of : 주로 데이터 변환 또는 매핑을 위해 사용되는 메소드
//      위 코드에서는 User 엔티티를 UserResponseDTO로 변환