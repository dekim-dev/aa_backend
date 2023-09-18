package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.ClinicRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicRecommendationRepository extends JpaRepository<ClinicRecommendation, Long> {
  Optional<ClinicRecommendation> findByUserIdAndClinicId(Long userId, Long clinicId);


}
