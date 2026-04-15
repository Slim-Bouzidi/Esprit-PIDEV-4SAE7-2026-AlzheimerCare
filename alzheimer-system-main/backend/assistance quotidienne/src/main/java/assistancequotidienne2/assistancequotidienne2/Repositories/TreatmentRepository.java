package assistancequotidienne2.assistancequotidienne2.Repositories;

import assistancequotidienne2.assistancequotidienne2.Entities.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    List<Treatment> findByPatientId(Long patientId);
}
