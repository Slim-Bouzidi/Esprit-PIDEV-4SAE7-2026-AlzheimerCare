package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.Rapport;
import assistancequotidienne2.assistancequotidienne2.Entities.StatutRapport;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.RapportRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RapportControllerWorkflowTest {

    @Mock private RapportRepository rapportRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private RapportController controller;

    @Test
    void shouldRejectReadTransitionWhenReportIsNotSent() {
        Rapport rapport = new Rapport();
        rapport.setId(1L);
        rapport.setStatut(StatutRapport.GENERE);

        when(rapportRepository.findById(1L)).thenReturn(Optional.of(rapport));

        ResponseEntity<Rapport> response = controller.marquerLuParSoignant(1L);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void shouldAllowReadTransitionWhenReportIsSent() {
        Rapport rapport = new Rapport();
        rapport.setId(2L);
        rapport.setStatut(StatutRapport.ENVOYE);

        when(rapportRepository.findById(2L)).thenReturn(Optional.of(rapport));
        when(rapportRepository.save(any(Rapport.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<Rapport> response = controller.marquerLuParSoignant(2L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(Boolean.TRUE.equals(response.getBody().getLuParSoignant()));
    }
}
