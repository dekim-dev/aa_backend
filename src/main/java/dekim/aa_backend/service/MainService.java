package dekim.aa_backend.service;

import dekim.aa_backend.dto.AdvertisementDTO;
import dekim.aa_backend.dto.UserInfoDTO;
import dekim.aa_backend.entity.Advertisement;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.AdvertisementRepository;
import dekim.aa_backend.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    public UserInfoDTO getUserInfo(Long userId) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                throw new RuntimeException("User not found");
            }
            User user = userOptional.get();
            return UserInfoDTO.of(user);
    }

    // 광고
    public List<AdvertisementDTO> getAllAdvertisements(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        List<Advertisement> advertisements = advertisementRepository.findAll();
        List<AdvertisementDTO> advertisementDTOs = advertisements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return advertisementDTOs;
    }

    private AdvertisementDTO convertToDTO(Advertisement advertisement) {
        AdvertisementDTO dto = new AdvertisementDTO();
        dto.setId(advertisement.getId());
        dto.setAdvertiser(advertisement.getAdvertiser());
        dto.setImgUrl(advertisement.getImgUrl());
        dto.setExpiresOn(advertisement.getExpiresOn());
        return dto;
    }
}
