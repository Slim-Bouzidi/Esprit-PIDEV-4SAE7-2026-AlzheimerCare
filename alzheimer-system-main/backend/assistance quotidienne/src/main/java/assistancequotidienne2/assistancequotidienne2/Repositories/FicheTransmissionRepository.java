package assistancequotidienne2.assistancequotidienne2.Repositories;

import assistancequotidienne2.assistancequotidienne2.Entities.FicheTransmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FicheTransmissionRepository extends JpaRepository<FicheTransmission, Long> {
    List<FicheTransmission> findByPatientId(Long patientId);
    List<FicheTransmission> findByPatientIdOrderByDateCreationDesc(Long patientId);
    List<FicheTransmission> findBySoignantId(Long soignantId);
    List<FicheTransmission> findByPatientIdAndDateFiche(Long patientId, LocalDate dateFiche);
    List<FicheTransmission> findByPatientIdAndDateFicheBetween(Long patientId, LocalDate debut, LocalDate fin);
    List<FicheTransmission> findByStatutAndDateFicheBetween(String statut, LocalDate debut, LocalDate fin);
}
