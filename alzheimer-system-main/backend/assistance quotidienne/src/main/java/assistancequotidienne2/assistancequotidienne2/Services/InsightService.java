package assistancequotidienne2.assistancequotidienne2.Services;

import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Entities.RapportHebdomadaire;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.RapportHebdomadaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InsightService {

    public static class InsightDto {
        private Long id;
        private String message;
        private String type;
        private LocalDateTime dateCreation;

        public InsightDto(Long id, String message, String type, LocalDateTime dateCreation) {
            this.id = id;
            this.message = message;
            this.type = type;
            this.dateCreation = dateCreation;
        }

        public Long getId() { return id; }
        public String getMessage() { return message; }
        public String getType() { return type; }
        public LocalDateTime getDateCreation() { return dateCreation; }
    }

    private final AtomicLong insightIdSequence = new AtomicLong(1);
    private final Map<Long, List<InsightDto>> interactionInsightsByPatient = new ConcurrentHashMap<>();

    @Autowired
    private RapportHebdomadaireRepository rapportHebdomadaireRepository;

    @Autowired
    private PatientRepository patientRepository;

    public List<InsightDto> getInsightsByPatient(Long patientId) {
        List<InsightDto> insights = new ArrayList<>();

        // 1) Derive clinical insights from latest weekly report
        List<RapportHebdomadaire> rapports = rapportHebdomadaireRepository.findByPatientIdOrderByDateDebutDesc(patientId);
        if (!rapports.isEmpty()) {
            RapportHebdomadaire latest = rapports.get(0);
            insights.addAll(buildInsightsFromLatestReport(latest));
        } else {
            insights.add(new InsightDto(
                    insightIdSequence.getAndIncrement(),
                    "Aucun rapport hebdomadaire disponible pour ce patient. Lancez la consolidation hebdomadaire.",
                    "INFO",
                    LocalDateTime.now()
            ));
        }

        // 2) Add user interaction insights (memoire assistee)
        List<InsightDto> interactionInsights = interactionInsightsByPatient.getOrDefault(patientId, List.of());
        insights.addAll(interactionInsights);

        insights.sort(Comparator.comparing(InsightDto::getDateCreation).reversed());
        return insights;
    }

    public void recordInteraction(Long patientId, String type, String details) {
        String normalized = type == null ? "" : type.trim().toUpperCase();
        String severity = "INFO";
        String message;

        if ("FAILURE".equals(normalized)) {
            severity = "WARNING";
            message = "Echec detecte dans la memoire assistee";
        } else {
            message = "Interaction memoire assistee enregistree avec succes";
        }

        if (details != null && !details.isBlank()) {
            message += " - " + details.trim();
        }

        InsightDto interactionInsight = new InsightDto(
                insightIdSequence.getAndIncrement(),
                message,
                severity,
                LocalDateTime.now()
        );

        interactionInsightsByPatient
                .computeIfAbsent(patientId, ignored -> new ArrayList<>())
                .add(0, interactionInsight);
    }

    public void triggerGlobalAnalysis() {
        List<Patient> patients = patientRepository.findAll();
        for (Patient patient : patients) {
            if (patient.getId() == null) {
                continue;
            }
            // Force a fresh insight snapshot by touching existing list.
            interactionInsightsByPatient.computeIfAbsent(patient.getId(), ignored -> new ArrayList<>());
        }
    }

    private List<InsightDto> buildInsightsFromLatestReport(RapportHebdomadaire latest) {
        List<InsightDto> insights = new ArrayList<>();
        LocalDateTime baseDate = latest.getDateCreation() != null ? latest.getDateCreation() : LocalDateTime.now();

        double medic = latest.getTauxObservanceMedicaments() != null ? latest.getTauxObservanceMedicaments() : 0.0;
        double repas = latest.getTauxObservanceRepas() != null ? latest.getTauxObservanceRepas() : 0.0;
        double rdv = latest.getTauxObservanceRendezVous() != null ? latest.getTauxObservanceRendezVous() : 0.0;

        String patientNom = latest.getPatientNom() != null ? latest.getPatientNom() : "Patient";

        if (medic < 50) {
            insights.add(new InsightDto(
                    insightIdSequence.getAndIncrement(),
                    "Risque critique: observance medicaments a " + Math.round(medic) + "% pour " + patientNom,
                    "CRITICAL",
                    baseDate.minusMinutes(2)
            ));
        } else if (medic < 80) {
            insights.add(new InsightDto(
                    insightIdSequence.getAndIncrement(),
                    "Alerte: observance medicaments faible (" + Math.round(medic) + "%)",
                    "WARNING",
                    baseDate.minusMinutes(2)
            ));
        } else {
            insights.add(new InsightDto(
                    insightIdSequence.getAndIncrement(),
                    "Observance medicaments satisfaisante (" + Math.round(medic) + "%)",
                    "INFO",
                    baseDate.minusMinutes(2)
            ));
        }

        if (repas < 60) {
            insights.add(new InsightDto(
                    insightIdSequence.getAndIncrement(),
                    "Alerte nutritionnelle: observance repas faible (" + Math.round(repas) + "%)",
                    "WARNING",
                    baseDate.minusMinutes(1)
            ));
        }

        if (rdv < 70) {
            insights.add(new InsightDto(
                    insightIdSequence.getAndIncrement(),
                    "Suivi rendez-vous insuffisant (" + Math.round(rdv) + "%)",
                    "WARNING",
                    baseDate
            ));
        } else {
            insights.add(new InsightDto(
                    insightIdSequence.getAndIncrement(),
                    "Suivi rendez-vous correct (" + Math.round(rdv) + "%)",
                    "INFO",
                    baseDate
            ));
        }

        String incidents = latest.getIncidentsNotables();
        if (incidents != null && !incidents.isBlank()) {
            insights.add(new InsightDto(
                    insightIdSequence.getAndIncrement(),
                    "Incidents notables signales: " + incidents,
                    "CRITICAL",
                    baseDate.plusSeconds(1)
            ));
        }

        return insights;
    }
}
