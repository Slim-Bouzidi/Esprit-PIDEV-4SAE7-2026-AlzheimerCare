package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Entities.RendezVous;
import assistancequotidienne2.assistancequotidienne2.Entities.StatutRendezVous;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.RendezVousRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RendezVousController.class)
@AutoConfigureMockMvc(addFilters = false)
class RendezVousControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RendezVousRepository rendezVousRepository;
    @MockBean
    private PatientRepository patientRepository;
    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldReturnBadRequestOnAppointmentConflict() throws Exception {
        Patient patient = new Patient();
        patient.setId(1L);
        Mockito.when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        RendezVous existing = new RendezVous();
        existing.setDateHeure(LocalDateTime.of(2026, 4, 21, 10, 0));
        existing.setDureeMinutes(60);
        existing.setStatut(StatutRendezVous.PLANIFIE);
        Mockito.when(rendezVousRepository.findByPatientId(1L)).thenReturn(List.of(existing));

        String payload = "{"
                + "\"patient\":{\"id\":1},"
                + "\"dateHeure\":\"2026-04-21T10:30:00\","
                + "\"dureeMinutes\":30,"
                + "\"statut\":\"PLANIFIE\""
                + "}";

        mockMvc.perform(post("/api/rendez-vous")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenJsonBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/api/rendez-vous")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not json"))
                .andExpect(status().isBadRequest());
    }
}
