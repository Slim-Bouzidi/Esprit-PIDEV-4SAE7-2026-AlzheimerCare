package assistancequotidienne2.assistancequotidienne2.Repositories;

import assistancequotidienne2.assistancequotidienne2.Entities.Rapport;
import assistancequotidienne2.assistancequotidienne2.Entities.TypeRapport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {
    List<Rapport> findByPatientId(Long patientId);
    List<Rapport> findByTypeRapport(TypeRapport type);
    List<Rapport> findByPatientIdOrderByDateGenerationDesc(Long patientId);
}
