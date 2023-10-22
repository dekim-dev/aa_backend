package dekim.aa_backend.dto;

import dekim.aa_backend.entity.MedicationList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DiaryDTO {
    private Long id;
    private String title;
    private String content;
    private String conclusion;
    private LocalDateTime createdAt;
    private List<MedicationList> medicationLists;
}
