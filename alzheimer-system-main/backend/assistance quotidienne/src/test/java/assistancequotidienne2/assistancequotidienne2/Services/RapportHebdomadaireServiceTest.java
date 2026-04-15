package assistancequotidienne2.assistancequotidienne2.Services;

import assistancequotidienne2.assistancequotidienne2.Entities.FicheTransmission;
import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Entities.RapportHebdomadaire;
import assistancequotidienne2.assistancequotidienne2.Repositories.FicheTransmissionRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.NotificationRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.RapportHebdomadaireRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RapportHebdomadaireServiceTest {

    @Mock
    private FicheTransmissionRepository ficheRepository;
    @Mock
    private RapportHebdomadaireRepository rapportHebdoRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private NotificationWsService notificationWsService;

    @InjectMocks
    private RapportHebdomadaireService service;

    @Test
    void shouldReturnNullWhenNoValidatedFichesExist() {
        LocalDate debut = LocalDate.now().minusDays(6);
        LocalDate fin = LocalDate.now();
        when(ficheRepository.findByPatientIdAndDateFicheBetween(1L, debut, fin)).thenReturn(List.of());

        RapportHebdomadaire result = service.consolider(1L, debut, fin, false);

        assertNull(result);
    }

    @Test
    void shouldGenerateWeeklyReportFromValidatedFiches() {
        LocalDate debut = LocalDate.of(2026, 4, 7);
        LocalDate fin = LocalDate.of(2026, 4, 13);

        FicheTransmission f1 = new FicheTransmission();
        f1.setId(10L);
        f1.setDateFiche(LocalDate.of(2026, 4, 8));
        f1.setStatut("envoye");
        f1.setObservanceMedicamentsJson("{\"totalPris\":2,\"totalPrevus\":4}");
        f1.setAlimentationJson("{\"appetit\":\"bon\"}");
        f1.setCommentaireLibre("etat stable");

        FicheTransmission f2 = new FicheTransmission();
        f2.setId(11L);
        f2.setDateFiche(LocalDate.of(2026, 4, 9));
        f2.setStatut("valide");
        f2.setObservanceMedicamentsJson("{\"totalPris\":3,\"totalPrevus\":4}");
        f2.setAlimentationJson("{\"appetit\":\"moyen\"}");
        f2.setCommentaireLibre("legere confusion");

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setNomComplet("Patient Demo");

        when(ficheRepository.findByPatientIdAndDateFicheBetween(1L, debut, fin)).thenReturn(List.of(f1, f2));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(rapportHebdoRepository.findByPatientIdAndDateDebutAndDateFin(1L, debut, fin)).thenReturn(List.of());
        when(rapportHebdoRepository.save(any(RapportHebdomadaire.class))).thenAnswer(inv -> inv.getArgument(0));

        RapportHebdomadaire result = service.consolider(1L, debut, fin, false);

        assertNotNull(result);
        assertEquals("Patient Demo", result.getPatientNom());
        assertEquals("[10,11]", result.getFormulaireIdsJson());
        assertNotNull(result.getObservationsGenerales());
        assertTrue(result.getObservationsGenerales().contains("2 fiche(s)"));
        assertTrue(result.getTauxObservanceMedicaments() > 60.0);
        assertEquals(100.0, result.getTauxObservanceRendezVous());
    }
}
