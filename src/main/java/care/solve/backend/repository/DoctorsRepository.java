package care.solve.backend.repository;

import care.solve.backend.entity.DoctorPrivate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorsRepository extends JpaRepository<DoctorPrivate, String> {
}
