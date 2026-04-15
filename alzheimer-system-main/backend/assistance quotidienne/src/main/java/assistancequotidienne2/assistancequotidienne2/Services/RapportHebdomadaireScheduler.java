package assistancequotidienne2.assistancequotidienne2.Services;

import assistancequotidienne2.assistancequotidienne2.Entities.*;
import assistancequotidienne2.assistancequotidienne2.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service de génération automatique des rapports hebdomadaires
 * Basé sur les fiches de transmission de la semaine
 * 
 * Planification: Tous les lundis à 09:00
 */
@Service
public class RapportHebdomadaireScheduler {

    @Autowired
    private FicheTransmissionRepository ficheRepository;

    @Autowired
    private RapportHebdomadaireRepository rapportHebdoRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationWsService notificationWsService;

    /**
     * Génération automatique des rapports hebdomadaires
     * Cron: Tous les lundis à 09:00 (PRODUCTION)
     * Cron: Toutes les minutes (TEST)
     * Format: seconde minute heure jour mois jour-de-la-semaine
     */
    @Scheduled(cron = "0 * * * * *")  // TEST: Toutes les minutes
    // @Scheduled(cron = "0 0 9 * * MON")  // PRODUCTION: Tous les lundis à 09:00
    @Transactional
    public void genererRapportsHebdomadaires() {
        System.out.println("🔄 [SCHEDULER] Génération automatique des rapports hebdomadaires - " + LocalDateTime.now());

        // TEST: Calculer la période de la semaine ACTUELLE (au lieu de la semaine dernière)
        LocalDate aujourdhui = LocalDate.now();
        LocalDate debutSemaineActuelle = aujourdhui.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finSemaineActuelle = debutSemaineActuelle.plusDays(6);

        System.out.println("📅 Période: " + debutSemaineActuelle + " → " + finSemaineActuelle);

        // Récupérer toutes les fiches envoyées de la semaine actuelle
        List<FicheTransmission> fichesEnvoyees = ficheRepository.findByStatutAndDateFicheBetween(
            "envoye", 
            debutSemaineActuelle, 
            finSemaineActuelle
        );

        System.out.println("📄 Fiches trouvées: " + fichesEnvoyees.size());

        // Grouper les fiches par patient
        Map<Long, List<FicheTransmission>> fichesByPatient = fichesEnvoyees.stream()
            .filter(f -> f.getPatient() != null && f.getPatient().getId() != null)
            .collect(Collectors.groupingBy(f -> f.getPatient().getId()));

        System.out.println("👥 Patients concernés: " + fichesByPatient.size());

        // Générer un rapport pour chaque patient
        int rapportsGeneres = 0;
        for (Map.Entry<Long, List<FicheTransmission>> entry : fichesByPatient.entrySet()) {
            Long patientId = entry.getKey();
            List<FicheTransmission> fichesPatient = entry.getValue();

            try {
                genererRapportPourPatient(patientId, fichesPatient, debutSemaineActuelle, finSemaineActuelle);
                rapportsGeneres++;
            } catch (Exception e) {
                System.err.println("❌ Erreur génération rapport pour patient " + patientId + ": " + e.getMessage());
            }
        }

        System.out.println("✅ [SCHEDULER] Rapports générés: " + rapportsGeneres + "/" + fichesByPatient.size());
    }

    /**
     * Génère un rapport hebdomadaire pour un patient spécifique
     */
    private void genererRapportPourPatient(Long patientId, List<FicheTransmission> fiches, 
                                          LocalDate dateDebut, LocalDate dateFin) {
        
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient non trouvé: " + patientId));

        // Vérifier si un rapport existe déjà pour cette période
        List<RapportHebdomadaire> existants = rapportHebdoRepository.findByPatientIdAndDateDebutAndDateFin(
            patientId, dateDebut, dateFin
        );

        if (!existants.isEmpty()) {
            System.out.println("⚠️ Rapport déjà existant pour patient " + patientId + " période " + dateDebut + " → " + dateFin);
            return;
        }

        // Créer le rapport hebdomadaire
        RapportHebdomadaire rapport = new RapportHebdomadaire();
        rapport.setPatient(patient);
        rapport.setPatientNom(patient.getNomComplet());
        
        // IMPORTANT: Définir le soignant (médecin référent du patient)
        if (patient.getSoignant() != null) {
            rapport.setSoignant(patient.getSoignant());
        }
        
        rapport.setDateDebut(dateDebut);
        rapport.setDateFin(dateFin);
        rapport.setDateCreation(LocalDateTime.now());

        // Calculer les statistiques à partir des fiches
        calculerStatistiques(rapport, fiches);

        // Générer les observations
        genererObservations(rapport, fiches);

        // Marquer comme envoyé automatiquement
        rapport.setEnvoyeAuMedecin(true);
        rapport.setDateEnvoi(LocalDateTime.now());

        // Sauvegarder le rapport
        RapportHebdomadaire saved = rapportHebdoRepository.save(rapport);
        System.out.println("✅ Rapport créé: ID=" + saved.getId() + " Patient=" + patient.getNomComplet());

        // Créer une notification pour le médecin
        envoyerNotificationMedecin(saved, patient);
    }

    /**
     * Calcule les statistiques d'observance à partir des fiches
     */
    private void calculerStatistiques(RapportHebdomadaire rapport, List<FicheTransmission> fiches) {
        int totalFiches = fiches.size();
        
        // Observance médicaments (moyenne des fiches)
        double moyenneMedic = fiches.stream()
            .filter(f -> f.getObservanceMedicamentsJson() != null)
            .mapToDouble(f -> extraireObservance(f.getObservanceMedicamentsJson()))
            .average()
            .orElse(0.0);

        // Observance repas (estimation basée sur alimentation)
        double moyenneRepas = fiches.stream()
            .filter(f -> f.getAlimentationJson() != null)
            .mapToDouble(f -> extraireObservanceRepas(f.getAlimentationJson()))
            .average()
            .orElse(0.0);

        // Observance RDV (100% si toutes les fiches sont envoyées)
        double moyenneRdv = 100.0;

        rapport.setTauxObservanceMedicaments(moyenneMedic);
        rapport.setTauxObservanceRepas(moyenneRepas);
        rapport.setTauxObservanceRendezVous(moyenneRdv);

        System.out.println("📊 Stats: Médic=" + rapport.getTauxObservanceMedicaments() + 
                          "% Repas=" + rapport.getTauxObservanceRepas() + 
                          "% RDV=" + rapport.getTauxObservanceRendezVous() + "%");
    }

    /**
     * Génère les observations textuelles à partir des fiches
     */
    private void genererObservations(RapportHebdomadaire rapport, List<FicheTransmission> fiches) {
        StringBuilder observations = new StringBuilder();
        observations.append("Rapport hebdomadaire automatique basé sur ").append(fiches.size()).append(" fiche(s) de transmission.\n\n");

        for (FicheTransmission fiche : fiches) {
            observations.append("📅 ").append(fiche.getDateFiche()).append(":\n");
            
            if (fiche.getCommentaireLibre() != null && !fiche.getCommentaireLibre().isEmpty()) {
                observations.append("  - ").append(fiche.getCommentaireLibre()).append("\n");
            }
            
            observations.append("\n");
        }

        rapport.setObservationsGenerales(observations.toString());
    }

    /**
     * Envoie une notification au médecin via WebSocket
     */
    private void envoyerNotificationMedecin(RapportHebdomadaire rapport, Patient patient) {
        try {
            if (patient.getSoignant() == null) {
                System.out.println("⚠️ Pas de médecin assigné au patient " + patient.getId());
                return;
            }

            Notification notif = new Notification();
            notif.setDestinataire(patient.getSoignant());
            notif.setPatient(patient);
            notif.setType("RAPPORT_HEBDOMADAIRE");
            notif.setTitre("Nouveau rapport hebdomadaire — " + patient.getNomComplet());
            notif.setMessage("Rapport hebdomadaire automatique du " + rapport.getDateDebut() + 
                           " au " + rapport.getDateFin() + " pour " + patient.getNomComplet() + 
                           ". Observance: Médic " + rapport.getTauxObservanceMedicaments() + 
                           "%, Repas " + rapport.getTauxObservanceRepas() + "%.");
            notif.setReferenceId(rapport.getId());
            notif.setReferenceType("RAPPORT_HEBDOMADAIRE");

            Notification saved = notificationRepository.save(notif);
            
            // Envoyer via WebSocket
            notificationWsService.notifyDoctor(saved);
            
            System.out.println("📨 Notification envoyée au médecin via WebSocket");
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi notification: " + e.getMessage());
        }
    }

    /**
     * Extrait le taux d'observance médicaments du JSON
     */
    private double extraireObservance(String json) {
        try {
            String totalPris = extractSimpleJson(json, "totalPris");
            String totalPrevus = extractSimpleJson(json, "totalPrevus");
            
            if (!totalPris.isEmpty() && !totalPrevus.isEmpty()) {
                double pris = Double.parseDouble(totalPris);
                double prevus = Double.parseDouble(totalPrevus);
                if (prevus > 0) {
                    return (pris / prevus) * 100.0;
                }
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return 100.0; // Par défaut
    }

    /**
     * Extrait le taux d'observance repas du JSON alimentation
     */
    private double extraireObservanceRepas(String json) {
        try {
            String appetit = extractSimpleJson(json, "appetit");
            
            // Mapping simple: Bon=100%, Moyen=70%, Faible=40%
            if (appetit.toLowerCase().contains("bon")) return 100.0;
            if (appetit.toLowerCase().contains("moyen")) return 70.0;
            if (appetit.toLowerCase().contains("faible")) return 40.0;
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return 80.0; // Par défaut
    }

    /**
     * Utilitaire pour extraire une valeur d'un JSON simple
     */
    private String extractSimpleJson(String json, String field) {
        if (json == null || json.isEmpty()) return "";
        String key = "\"" + field + "\"";
        int idx = json.indexOf(key);
        if (idx < 0) return "";
        int colonIdx = json.indexOf(":", idx + key.length());
        if (colonIdx < 0) return "";
        int valStart = colonIdx + 1;
        while (valStart < json.length() && json.charAt(valStart) == ' ') valStart++;
        if (valStart >= json.length()) return "";
        char c = json.charAt(valStart);
        if (c == '"') {
            int valEnd = json.indexOf("\"", valStart + 1);
            return valEnd > valStart ? json.substring(valStart + 1, valEnd) : "";
        }
        int valEnd = valStart;
        while (valEnd < json.length() && json.charAt(valEnd) != ',' && json.charAt(valEnd) != '}') {
            valEnd++;
        }
        return json.substring(valStart, valEnd).trim();
    }
}
