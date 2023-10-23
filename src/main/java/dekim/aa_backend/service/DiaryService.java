package dekim.aa_backend.service;

import dekim.aa_backend.dto.DiaryDTO;
import dekim.aa_backend.entity.Diary;
import dekim.aa_backend.entity.MedicationList;
import dekim.aa_backend.entity.Post;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.DiaryRepository;
import dekim.aa_backend.persistence.MedicationListRepository;
import dekim.aa_backend.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MedicationListRepository medicationListRepository;

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

    public DiaryDTO fetchDiaryById(Long userId, Long diaryId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        Optional<Diary> diaryOptional = diaryRepository.findById(diaryId);
        if (diaryOptional.isPresent()) {
            Diary diary = diaryOptional.get();
            DiaryDTO diaryDTO = new DiaryDTO();
            diaryDTO.setId(diary.getId());
            diaryDTO.setCreatedAt(diary.getCreatedAt());
            diaryDTO.setTitle(diary.getTitle());
            diaryDTO.setContent(diary.getContent());
            diaryDTO.setConclusion(diary.getConclusion());
            diaryDTO.setMedicationLists(diary.getMedicationLists());
            diaryDTO.setUpdatedAt(diary.getUpdatedAt());
            return diaryDTO;
        } else {
            throw new EntityNotFoundException("Diary not found");
        }
    }

    public String deleteDiaryById(Long userId, Long diaryId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        Optional<Diary> diaryOptional = diaryRepository.findById(diaryId);
        if (diaryOptional.isPresent()) {
            diaryRepository.deleteById(diaryId);
            return "다이어리 삭제 완료";
        } else {
            throw new IllegalArgumentException("다이어리 삭제 실패");
        }
    }

    @Transactional
    public Diary updateDiary(Long userId, Long diaryId, DiaryDTO diaryDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        Optional<Diary> diaryOptional = diaryRepository.findById(diaryId);
        System.out.println(diaryOptional);

        if (diaryOptional.isPresent()) {
            Diary updatedDiary = diaryOptional.get();
            updatedDiary.setTitle(diaryDTO.getTitle());
            updatedDiary.setContent(diaryDTO.getContent());
            updatedDiary.setConclusion(diaryDTO.getConclusion());
            updatedDiary.setCreatedAt(diaryDTO.getCreatedAt());
            updatedDiary.setUpdatedAt(LocalDateTime.now());

            // MedicationList 업데이트
            List<MedicationList> updatedMedicationLists = diaryDTO.getMedicationLists();
            List<MedicationList> existingMedicationLists = updatedDiary.getMedicationLists();

            // 추가 & 업데이트
            for (MedicationList updatedMedication : updatedMedicationLists) {
                Optional<MedicationList> existingMedicationOptional = existingMedicationLists.stream()
                        .filter(existingMedication -> existingMedication.getId().equals(updatedMedication.getId()))
                        .findFirst();

                if (existingMedicationOptional.isPresent()) {
                    // 업데이트
                    MedicationList existingMedication = existingMedicationOptional.get();
                    existingMedication.setMed(updatedMedication.getMed());
                    existingMedication.setTakenAt(updatedMedication.getTakenAt());
                } else {
                    // 추가
                    updatedMedication.setDiary(updatedDiary);
                    updatedMedication.setUser(userOptional.get());
                    medicationListRepository.save(updatedMedication);
                }
            }
            return diaryRepository.save(updatedDiary);
        } else {
            throw new EntityNotFoundException("존재하지 않는 다이어리 입니다.");
        }
    }

    public Long deleteMedicationListById(Long userId, Long medId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        Optional<MedicationList> medicationListOptional = medicationListRepository.findById(medId);
        if (medicationListOptional.isPresent()) {
            medicationListRepository.deleteById(medId);
            return medId;
        } else {
            throw new IllegalArgumentException("MedicationList 삭제 실패");
        }
    }

    public void deleteMultipleDiaries( Long userId, List<Long> diaryIds) {
        for (Long diaryId : diaryIds) {
            Optional<Diary> diaryOptional = diaryRepository.findById(diaryId);
            if (diaryOptional.isPresent()) {
                Diary diary = diaryOptional.get();
                if (!diary.getUser().getId().equals(userId)) {
                    throw new RuntimeException("You are not authorized to delete diary with id: " + diaryId);
                }
                diaryRepository.deleteById(diaryId);
            }
        }
    }

}
