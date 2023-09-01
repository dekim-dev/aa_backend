package dekim.aa_backend.service;

import dekim.aa_backend.entity.Diary;
import dekim.aa_backend.entity.User;
import dekim.aa_backend.persistence.DiaryRepository;
import dekim.aa_backend.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;
    @Autowired
    private UserRepository userRepository;

    public Diary createDiary(Diary diary) {
        Diary newDiary = diaryRepository.save(diary);
        return newDiary;
    }


    public List<Diary> fetchAllDiaries() {
        List<Diary> diaryList = diaryRepository.findAll();
        return diaryList;
    }

}
