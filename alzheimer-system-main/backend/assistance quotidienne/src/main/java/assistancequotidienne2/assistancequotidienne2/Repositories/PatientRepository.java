package assistancequotidienne2.assistancequotidienne2.Repositories;

import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    // Trouver par nom complet
    List<Patient> findByNomCompletContaining(String nom);
    
    // Trouver par téléphone
    List<Patient> findByNumeroDeTelephone(String numeroDeTelephone);
    
    // Trouver les patients actifs
    List<Patient> findByActifTrue();

    // Trouver les patients par soignant
    List<Patient> findBySoignantId(Long soignantId);
}
