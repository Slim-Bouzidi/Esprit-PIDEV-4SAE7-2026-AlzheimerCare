package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Services.InsightService;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InsightController.class)
@AutoConfigureMockMvc(addFilters = false)
class InsightControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InsightService insightService;

    @Test
    void shouldReturnPatientInsights() throws Exception {
        InsightService.InsightDto dto =
                new InsightService.InsightDto(1L, "Insight test", "INFO", LocalDateTime.now());
        Mockito.when(insightService.getInsightsByPatient(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/insights/patient/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Insight test"))
                .andExpect(jsonPath("$[0].type").value("INFO"));
    }

    @Test
    void shouldAcceptInteractionRecording() throws Exception {
        mockMvc.perform(post("/api/insights/interaction")
                        .param("patientId", "1")
                        .param("type", "FAILURE")
                        .param("details", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(insightService).recordInteraction(eq(1L), eq("FAILURE"), eq("test"));
    }

    @Test
    void shouldTriggerGlobalAnalysisEndpoint() throws Exception {
        mockMvc.perform(post("/api/insights/analyze/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(insightService).triggerGlobalAnalysis();
    }

    @Test
    void shouldReturnJsonArrayWithExpectedFieldsForInsights() throws Exception {
        InsightService.InsightDto dto = new InsightService.InsightDto(
                9L, "Message detaille", "WARNING", java.time.LocalDateTime.of(2026, 3, 1, 8, 30));
        Mockito.when(insightService.getInsightsByPatient(2L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/insights/patient/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(9))
                .andExpect(jsonPath("$[0].message").value("Message detaille"))
                .andExpect(jsonPath("$[0].type").value("WARNING"));
    }
}
