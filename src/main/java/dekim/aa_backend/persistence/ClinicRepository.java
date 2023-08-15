package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

  boolean existsByHpid(String hpid);
  List<Clinic> findByNameContaining(String keyword);
}
