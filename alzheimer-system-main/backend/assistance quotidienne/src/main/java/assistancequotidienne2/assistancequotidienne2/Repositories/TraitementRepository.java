package assistancequotidienne2.assistancequotidienne2.Repositories;

import assistancequotidienne2.assistancequotidienne2.Entities.Traitement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraitementRepository extends JpaRepository<Traitement, Long> {
    // Trouver tous les traitements actifs d'un patient
    List<Traitement> findByPatientIdAndActifTrue(Long patientId);
    
    // Trouver par nom de médicament
    List<Traitement> findByNomMedicamentContaining(String nom);
}
