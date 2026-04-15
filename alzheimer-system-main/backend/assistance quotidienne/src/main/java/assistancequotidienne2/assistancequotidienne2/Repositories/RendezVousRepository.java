package assistancequotidienne2.assistancequotidienne2.Repositories;

import assistancequotidienne2.assistancequotidienne2.Entities.RendezVous;
import assistancequotidienne2.assistancequotidienne2.Entities.StatutRendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    // RDV d'un patient
    List<RendezVous> findByPatientId(Long patientId);
    
    // RDV d'un soignant
    List<RendezVous> findBySoignantId(Long soignantId);
    
    // RDV à venir
    List<RendezVous> findByDateHeureAfterAndStatut(LocalDateTime date, StatutRendezVous statut);
    
    // RDV du jour
    List<RendezVous> findByDateHeureBetween(LocalDateTime debut, LocalDateTime fin);
}
