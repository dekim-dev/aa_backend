package dekim.aa_backend.service;

import dekim.aa_backend.dto.UserInfoDTO;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MainService {

    private final UserRepository userRepository;
    public UserInfoDTO getUserInfo(Long userId) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                throw new RuntimeException("User not found");
            }
            User user = userOptional.get();
            return UserInfoDTO.of(user);
    }
}
