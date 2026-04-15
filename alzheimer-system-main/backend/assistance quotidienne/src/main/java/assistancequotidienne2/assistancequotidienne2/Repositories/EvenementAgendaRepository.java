package assistancequotidienne2.assistancequotidienne2.Repositories;

import assistancequotidienne2.assistancequotidienne2.Entities.EvenementAgenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EvenementAgendaRepository extends JpaRepository<EvenementAgenda, Long> {
    List<EvenementAgenda> findByPatientId(Long patientId);
    List<EvenementAgenda> findByPatientIdAndDateEvenement(Long patientId, LocalDate date);
    List<EvenementAgenda> findByDateEvenementBetween(LocalDate debut, LocalDate fin);
    List<EvenementAgenda> findByPatientIdAndDateEvenementBetween(Long patientId, LocalDate debut, LocalDate fin);
    List<EvenementAgenda> findByDateEvenement(LocalDate date);
}
