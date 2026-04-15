package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.FicheTransmission;
import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Repositories.FicheTransmissionRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.NotificationRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.RapportRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.UserRepository;
import assistancequotidienne2.assistancequotidienne2.Services.FichePdfService;
import assistancequotidienne2.assistancequotidienne2.Services.NotificationWsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FicheTransmissionControllerTest {

    @Mock private FicheTransmissionRepository ficheRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private RapportRepository rapportRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;
    @Mock private FichePdfService fichePdfService;
    @Mock private NotificationWsService notificationWsService;

    @InjectMocks
    private FicheTransmissionController controller;

    @Test
    void shouldRejectDuplicateFicheForSamePatientAndDate() {
        Patient patient = new Patient();
        patient.setId(1L);

        FicheTransmission fiche = new FicheTransmission();
        fiche.setPatient(patient);
        fiche.setDateFiche(LocalDate.of(2026, 4, 15));

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(ficheRepository.findByPatientIdAndDateFiche(1L, fiche.getDateFiche()))
                .thenReturn(List.of(new FicheTransmission()));

        ResponseEntity<FicheTransmission> response = controller.create(fiche);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void shouldCreateBrouillonWhenNoDuplicateExists() {
        Patient patient = new Patient();
        patient.setId(1L);

        FicheTransmission fiche = new FicheTransmission();
        fiche.setPatient(patient);
        fiche.setDateFiche(LocalDate.of(2026, 4, 16));

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(ficheRepository.findByPatientIdAndDateFiche(1L, fiche.getDateFiche())).thenReturn(List.of());
        when(ficheRepository.save(any(FicheTransmission.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<FicheTransmission> response = controller.create(fiche);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("brouillon", response.getBody().getStatut());
    }

    @Test
    void shouldThrowWhenPatientReferenceDoesNotExist() {
        Patient ref = new Patient();
        ref.setId(404L);
        FicheTransmission fiche = new FicheTransmission();
        fiche.setPatient(ref);

        when(patientRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> controller.create(fiche));
    }

    @Test
    void shouldRejectSendWhenFicheIsNotSigned() {
        FicheTransmission fiche = new FicheTransmission();
        fiche.setId(12L);
        fiche.setSignatureSoignant(false);

        when(ficheRepository.findById(12L)).thenReturn(Optional.of(fiche));

        ResponseEntity<FicheTransmission> response = controller.marquerEnvoye(12L);

        assertEquals(400, response.getStatusCodeValue());
    }
}
