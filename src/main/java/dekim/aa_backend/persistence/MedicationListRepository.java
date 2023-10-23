package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.MedicationList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationListRepository extends JpaRepository<MedicationList, Long> {

}
