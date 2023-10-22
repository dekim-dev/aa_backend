package dekim.aa_backend.service;

import dekim.aa_backend.dto.DiaryDTO;
import dekim.aa_backend.entity.Diary;
import dekim.aa_backend.entity.MedicationList;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.DiaryRepository;
import dekim.aa_backend.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private UserRepository userRepository;

    public Diary createDiary(Diary diary, Long userId) {
        // 1. 사용자 아이디를 통해 사용자 정보 조회
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        diary.setUser(user);

        // 2. MedicationList 목록을 Diary와 연결
        List<MedicationList> medicationList = diary.getMedicationLists();
        for (MedicationList medication : medicationList) {
            medication.setDiary(diary);
            medication.setUser(user);
        }

        // 3. Diary 엔티티 저장
        Diary newDiary = diaryRepository.save(diary);
        return newDiary;
    }



    public Page<DiaryDTO> fetchAllDiaries(Long userId, int page, int pageSize) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<Diary> diaryPage = diaryRepository.findByUserOrderByCreatedAtDesc(user, pageRequest);

        return diaryPage.map(diary -> {
            DiaryDTO dto = new DiaryDTO();
            dto.setId(diary.getId());
            dto.setTitle(diary.getTitle());
            dto.setContent(diary.getContent());
            dto.setConclusion(diary.getConclusion());
            dto.setCreatedAt(diary.getCreatedAt());
            dto.setMedicationLists(diary.getMedicationLists());
            return dto;
        });
    }


    public List<Diary> fetchLatestThreeDiaries(Long userId) {
        // 사용자의 정보 확인
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        // 사용자와 관련된 다이어리만 조회
        List<Diary> diaryList = diaryRepository.findTop3ByUserOrderByCreatedAtDesc(user);

        return diaryList;
    }



}
