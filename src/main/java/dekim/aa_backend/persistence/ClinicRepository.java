package dekim.aa_backend.persistence;

import dekim.aa_backend.entity.Clinic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

  boolean existsByHpid(String hpid);
  Page<Clinic> findByNameContaining(String keyword, Pageable pageable);
  Page<Clinic> findByAddressContaining(String address, Pageable pageable);
  Optional<Clinic> findById(Long id);
}
