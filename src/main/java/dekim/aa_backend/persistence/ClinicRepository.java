package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

  boolean existsByHpid(String hpid);
}
