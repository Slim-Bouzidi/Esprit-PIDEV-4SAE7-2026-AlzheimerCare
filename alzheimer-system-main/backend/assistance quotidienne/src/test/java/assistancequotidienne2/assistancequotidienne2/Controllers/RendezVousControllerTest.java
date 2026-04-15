package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Entities.RendezVous;
import assistancequotidienne2.assistancequotidienne2.Entities.StatutRendezVous;
import assistancequotidienne2.assistancequotidienne2.Entities.User;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.RendezVousRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RendezVousControllerTest {

    @Mock private RendezVousRepository rendezVousRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private RendezVousController controller;

    @Test
    void shouldRejectCreationWhenPatientHasTimeConflict() {
        Patient patient = new Patient();
        patient.setId(1L);
        User soignant = new User();
        soignant.setId(2L);

        RendezVous existing = new RendezVous();
        existing.setPatient(patient);
        existing.setDateHeure(LocalDateTime.of(2026, 4, 20, 10, 0));
        existing.setDureeMinutes(60);
        existing.setStatut(StatutRendezVous.PLANIFIE);

        RendezVous incoming = new RendezVous();
        incoming.setPatient(patient);
        incoming.setSoignant(soignant);
        incoming.setDateHeure(LocalDateTime.of(2026, 4, 20, 10, 30));
        incoming.setDureeMinutes(30);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(soignant));
        when(rendezVousRepository.findByPatientId(1L)).thenReturn(List.of(existing));
        when(rendezVousRepository.findBySoignantId(2L)).thenReturn(List.of());

        ResponseEntity<RendezVous> response = controller.create(incoming);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void shouldCreateAppointmentWhenNoConflict() {
        Patient patient = new Patient();
        patient.setId(1L);

        RendezVous incoming = new RendezVous();
        incoming.setPatient(patient);
        incoming.setDateHeure(LocalDateTime.of(2026, 4, 20, 12, 0));
        incoming.setDureeMinutes(30);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(rendezVousRepository.findByPatientId(1L)).thenReturn(List.of());
        when(rendezVousRepository.save(any(RendezVous.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<RendezVous> response = controller.create(incoming);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldRejectCreationWhenAnotherAppointmentStartsAtExactSameTime() {
        Patient patient = new Patient();
        patient.setId(3L);

        RendezVous existing = new RendezVous();
        existing.setId(50L);
        existing.setPatient(patient);
        existing.setDateHeure(LocalDateTime.of(2026, 5, 1, 14, 0));
        existing.setDureeMinutes(30);
        existing.setStatut(StatutRendezVous.PLANIFIE);

        RendezVous incoming = new RendezVous();
        incoming.setPatient(patient);
        incoming.setDateHeure(LocalDateTime.of(2026, 5, 1, 14, 0));
        incoming.setDureeMinutes(30);

        when(patientRepository.findById(3L)).thenReturn(Optional.of(patient));
        when(rendezVousRepository.findByPatientId(3L)).thenReturn(List.of(existing));

        ResponseEntity<RendezVous> response = controller.create(incoming);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void shouldThrowWhenPatientMissingOnCreate() {
        RendezVous incoming = new RendezVous();
        Patient patient = new Patient();
        patient.setId(99L);
        incoming.setPatient(patient);

        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> controller.create(incoming));
    }
}
