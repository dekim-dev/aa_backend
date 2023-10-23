package dekim.aa_backend.dto;

import dekim.aa_backend.entity.MedicationList;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class DiaryDTO {
    private Long id;
    private String title;
    private String content;
    private String conclusion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MedicationList> medicationLists;

    public DiaryDTO() {

    }
}
