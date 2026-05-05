package assistancequotidienne2.assistancequotidienne2.Repositories;

import assistancequotidienne2.assistancequotidienne2.Entities.MemoireAssistee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemoireAssisteeRepository extends JpaRepository<MemoireAssistee, Long> {
    Optional<MemoireAssistee> findByPatientId(Long patientId);
}
