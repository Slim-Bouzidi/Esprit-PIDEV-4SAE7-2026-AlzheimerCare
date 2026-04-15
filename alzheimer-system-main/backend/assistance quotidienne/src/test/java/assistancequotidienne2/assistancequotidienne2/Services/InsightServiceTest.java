package assistancequotidienne2.assistancequotidienne2.Services;

import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Entities.RapportHebdomadaire;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.RapportHebdomadaireRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsightServiceTest {

    @Mock
    private RapportHebdomadaireRepository rapportHebdomadaireRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private InsightService insightService;

    @Test
    void shouldReturnInfoInsightWhenNoWeeklyReportExists() {
        when(rapportHebdomadaireRepository.findByPatientIdOrderByDateDebutDesc(1L)).thenReturn(List.of());

        List<InsightService.InsightDto> insights = insightService.getInsightsByPatient(1L);

        assertFalse(insights.isEmpty());
        assertEquals("INFO", insights.get(0).getType());
        assertTrue(insights.get(0).getMessage().contains("Aucun rapport hebdomadaire"));
    }

    @Test
    void shouldGenerateCriticalAndWarningInsightsFromLowObservanceAndIncidents() {
        RapportHebdomadaire report = new RapportHebdomadaire();
        report.setPatientNom("Patient Test");
        report.setTauxObservanceMedicaments(40.0);
        report.setTauxObservanceRepas(50.0);
        report.setTauxObservanceRendezVous(60.0);
        report.setIncidentsNotables("Chute nocturne");
        report.setDateCreation(LocalDateTime.now());

        when(rapportHebdomadaireRepository.findByPatientIdOrderByDateDebutDesc(2L)).thenReturn(List.of(report));

        List<InsightService.InsightDto> insights = insightService.getInsightsByPatient(2L);

        assertTrue(insights.stream().anyMatch(i -> "CRITICAL".equals(i.getType()) && i.getMessage().contains("Risque critique")));
        assertTrue(insights.stream().anyMatch(i -> "WARNING".equals(i.getType()) && i.getMessage().contains("nutritionnelle")));
        assertTrue(insights.stream().anyMatch(i -> "CRITICAL".equals(i.getType()) && i.getMessage().contains("Incidents notables")));
    }

    @Test
    void shouldRecordFailureInteractionAsWarningInsight() {
        when(rapportHebdomadaireRepository.findByPatientIdOrderByDateDebutDesc(3L)).thenReturn(List.of());

        insightService.recordInteraction(3L, "FAILURE", "oubli de tache");

        List<InsightService.InsightDto> insights = insightService.getInsightsByPatient(3L);
        assertTrue(
                insights.stream().anyMatch(i ->
                        "WARNING".equals(i.getType())
                                && i.getMessage().contains("Echec detecte")
                                && i.getMessage().contains("oubli de tache"))
        );
    }

    @Test
    void shouldInitializeGlobalAnalysisForAllPatientsWithoutCrashing() {
        Patient p1 = new Patient();
        p1.setId(10L);
        Patient p2 = new Patient();
        p2.setId(11L);

        when(patientRepository.findAll()).thenReturn(List.of(p1, p2));

        assertDoesNotThrow(() -> insightService.triggerGlobalAnalysis());
    }

    @Test
    void shouldReturnStableInfoOnlyInsightsWhenObservanceIsHighAndNoIncidents() {
        RapportHebdomadaire report = new RapportHebdomadaire();
        report.setPatientNom("Dupont");
        report.setTauxObservanceMedicaments(95.0);
        report.setTauxObservanceRepas(75.0);
        report.setTauxObservanceRendezVous(85.0);
        report.setIncidentsNotables(null);
        report.setDateCreation(LocalDateTime.now());

        when(rapportHebdomadaireRepository.findByPatientIdOrderByDateDebutDesc(20L)).thenReturn(List.of(report));

        List<InsightService.InsightDto> insights = insightService.getInsightsByPatient(20L);

        assertTrue(insights.stream().noneMatch(i -> "CRITICAL".equals(i.getType())));
        assertTrue(insights.stream().anyMatch(i -> "INFO".equals(i.getType()) && i.getMessage().contains("Observance medicaments satisfaisante")));
        assertTrue(insights.stream().anyMatch(i -> "INFO".equals(i.getType()) && i.getMessage().contains("Suivi rendez-vous correct")));
    }

    @Test
    void shouldThrowWhenPatientIdIsNullBecauseInsightsAreKeyedByPatient() {
        assertThrows(NullPointerException.class, () -> insightService.getInsightsByPatient(null));
    }

    @Test
    void shouldGenerateMaximumSeverityInsightsForExtremeLowObservance() {
        RapportHebdomadaire report = new RapportHebdomadaire();
        report.setPatientNom("Edge");
        report.setTauxObservanceMedicaments(0.0);
        report.setTauxObservanceRepas(0.0);
        report.setTauxObservanceRendezVous(0.0);
        report.setIncidentsNotables("  ");
        report.setDateCreation(LocalDateTime.now());

        when(rapportHebdomadaireRepository.findByPatientIdOrderByDateDebutDesc(21L)).thenReturn(List.of(report));

        List<InsightService.InsightDto> insights = insightService.getInsightsByPatient(21L);

        assertTrue(insights.stream().anyMatch(i -> "CRITICAL".equals(i.getType()) && i.getMessage().contains("Risque critique")));
        assertTrue(insights.stream().anyMatch(i -> "WARNING".equals(i.getType()) && i.getMessage().contains("nutritionnelle")));
        assertTrue(insights.stream().anyMatch(i -> "WARNING".equals(i.getType()) && i.getMessage().contains("Suivi rendez-vous insuffisant")));
    }

    @Test
    void shouldRecordSuccessfulMemoireAssisteeInteractionAsInfo() {
        when(rapportHebdomadaireRepository.findByPatientIdOrderByDateDebutDesc(30L)).thenReturn(List.of());

        insightService.recordInteraction(30L, "SUCCESS", "prise de repas");

        List<InsightService.InsightDto> insights = insightService.getInsightsByPatient(30L);
        assertTrue(insights.stream().anyMatch(i ->
                "INFO".equals(i.getType())
                        && i.getMessage().contains("memoire assistee")
                        && i.getMessage().contains("prise de repas")));
    }

    @Test
    void shouldThrowWhenRecordingInteractionWithNullPatientId() {
        assertThrows(NullPointerException.class, () -> insightService.recordInteraction(null, "OK", null));
    }

    @Test
    void shouldSkipPatientsWithNullIdDuringGlobalAnalysis() {
        Patient noId = new Patient();
        Patient ok = new Patient();
        ok.setId(5L);
        when(patientRepository.findAll()).thenReturn(List.of(noId, ok));

        assertDoesNotThrow(() -> insightService.triggerGlobalAnalysis());
    }
}
