package dekim.aa_backend.service;

import dekim.aa_backend.entity.Diary;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.DiaryRepository;
import dekim.aa_backend.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private UserRepository userRepository;

    public Diary createDiary(Diary diary, Long userId) {
        // 2. 사용자 아이디를 통해 사용자 정보 조회
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        diary.setUser(user);

        Diary newDiary = diaryRepository.save(diary);
        return newDiary;
    }

    public List<Diary> fetchAllDiaries(Long userId) {
        // 사용자의 정보 확인
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        // 사용자와 관련된 다이어리만 조회
        List<Diary> diaryList = diaryRepository.findByUser(user);

        return diaryList;
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
