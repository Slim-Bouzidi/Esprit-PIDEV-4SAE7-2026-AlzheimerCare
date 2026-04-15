package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Entities.Rapport;
import assistancequotidienne2.assistancequotidienne2.Entities.StatutRapport;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.RapportRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RapportController.class)
@AutoConfigureMockMvc(addFilters = false)
class RapportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RapportRepository rapportRepository;
    @MockBean
    private PatientRepository patientRepository;
    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldReturnBadRequestWhenReportPeriodIsInvalid() throws Exception {
        Patient patient = new Patient();
        patient.setId(1L);
        Mockito.when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        String payload = "{"
                + "\"patient\":{\"id\":1},"
                + "\"soignant\":{\"id\":2},"
                + "\"typeRapport\":\"HEBDOMADAIRE\","
                + "\"periodeDebut\":\"2026-04-20\","
                + "\"periodeFin\":\"2026-04-10\","
                + "\"titre\":\"Test\","
                + "\"contenuTexte\":\"Contenu\""
                + "}";

        mockMvc.perform(post("/api/rapports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenTryingToMarkUnreadWorkflowOnNonSentReport() throws Exception {
        Rapport rapport = new Rapport();
        rapport.setId(99L);
        rapport.setStatut(StatutRapport.GENERE);
        Mockito.when(rapportRepository.findById(99L)).thenReturn(Optional.of(rapport));

        mockMvc.perform(patch("/api/rapports/99/lu"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnRapportsListAsJson() throws Exception {
        Rapport rapport = new Rapport();
        rapport.setId(3L);
        rapport.setTitre("Hebdo");
        Mockito.when(rapportRepository.findAll()).thenReturn(List.of(rapport));

        mockMvc.perform(get("/api/rapports").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].titre").value("Hebdo"));
    }

    @Test
    void shouldReturnBadRequestWhenPostBodyIsNotValidJson() throws Exception {
        mockMvc.perform(post("/api/rapports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest());
    }
}
