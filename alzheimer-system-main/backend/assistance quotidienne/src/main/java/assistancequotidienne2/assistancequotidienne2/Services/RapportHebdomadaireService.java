package assistancequotidienne2.assistancequotidienne2.Services;

import assistancequotidienne2.assistancequotidienne2.Entities.*;
import assistancequotidienne2.assistancequotidienne2.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RapportHebdomadaireService {

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
     * Consolide les fiches de transmission en un rapport hebdomadaire
     * Garantit qu'un seul rapport existe par patient et par période (semaine)
     */
    @Transactional
    public RapportHebdomadaire consolider(Long patientId, LocalDate debut, LocalDate fin, boolean autoSend) {
        
        // 1. Récupérer les fiches de la période pour ce patient
        List<FicheTransmission> fiches = ficheRepository.findByPatientIdAndDateFicheBetween(patientId, debut, fin);
        
        // 2. Filtrer uniquement les fiches finalisées ("envoye" ou "valide")
        List<FicheTransmission> fichesValidees = fiches.stream()
                .filter(f -> "envoye".equals(f.getStatut()) || "valide".equals(f.getStatut()))
                .toList();

        // 3. S'il n'y a aucune fiche validée, on ne génère pas de rapport (évite les rapports vides)
        if (fichesValidees.isEmpty()) {
            System.out.println("ℹ️ Aucun fiche validée pour le patient " + patientId + " entre " + debut + " et " + fin);
            return null;
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé: " + patientId));

        // 4. Rechercher si un rapport existe déjà pour cet UNIQUE duo Patient + Période
        RapportHebdomadaire rapport = rapportHebdoRepository
                .findByPatientIdAndDateDebutAndDateFin(patientId, debut, fin)
                .stream().findFirst().orElse(new RapportHebdomadaire());

        // 5. Si le rapport a déjà été officiellement envoyé au médecin, on ne le modifie plus
        if (Boolean.TRUE.equals(rapport.getEnvoyeAuMedecin())) {
            return rapport;
        }

        // 6. Mise à jour ou initialisation des métadonnées
        rapport.setPatient(patient);
        rapport.setPatientNom(patient.getNomComplet());
        rapport.setSoignant(patient.getSoignant());
        rapport.setDateDebut(debut);
        rapport.setDateFin(fin);
        
        if (rapport.getId() == null) {
            rapport.setDateCreation(LocalDateTime.now());
        }

        // 7. Consolidation des IDs de fiches (pour référence)
        StringBuilder idsJson = new StringBuilder("[");
        for (int i = 0; i < fichesValidees.size(); i++) {
            if (i > 0) idsJson.append(",");
            idsJson.append(fichesValidees.get(i).getId());
        }
        idsJson.append("]");
        rapport.setFormulaireIdsJson(idsJson.toString());

        // 8. Calcul des statistiques d'observance agrégées
        calculerStatistiques(rapport, fichesValidees);
        
        // 9. Synthèse textuelle des observations de la semaine
        genererObservations(rapport, fichesValidees);

        // 10. Activation de l'envoi (si déclenché par le scheduler le dimanche)
        if (autoSend) {
            rapport.marquerEnvoye();
            envoyerNotificationMedecin(rapport, patient);
        }

        System.out.println("✅ Rapport hebdomadaire consolidé pour " + patient.getNomComplet() + " (" + fichesValidees.size() + " fiches)");
        return rapportHebdoRepository.save(rapport);
    }


    private void calculerStatistiques(RapportHebdomadaire rapport, List<FicheTransmission> fiches) {
        if (fiches.isEmpty()) {
            rapport.setTauxObservanceMedicaments(0.0);
            rapport.setTauxObservanceRepas(0.0);
            rapport.setTauxObservanceRendezVous(0.0);
            return;
        }

        double sumMedic = 0;
        double sumRepas = 0;
        int countMedic = 0;
        int countRepas = 0;

        for (FicheTransmission f : fiches) {
            Double m = extraireObservance(f.getObservanceMedicamentsJson());
            if (m != null) { sumMedic += m; countMedic++; }

            Double r = extraireObservanceRepas(f.getAlimentationJson());
            if (r != null) { sumRepas += r; countRepas++; }
        }

        rapport.setTauxObservanceMedicaments(countMedic > 0 ? (sumMedic / countMedic) : 100.0);
        rapport.setTauxObservanceRepas(countRepas > 0 ? (sumRepas / countRepas) : 80.0);
        rapport.setTauxObservanceRendezVous(100.0);
    }

    private void genererObservations(RapportHebdomadaire rapport, List<FicheTransmission> fiches) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rapport consolidé à partir de ").append(fiches.size()).append(" fiche(s) de transmission.\n\n");

        for (FicheTransmission f : fiches) {
            sb.append("◈ ").append(f.getDateFiche()).append(" : ");
            if (f.getCommentaireLibre() != null && !f.getCommentaireLibre().trim().isEmpty()) {
                sb.append(f.getCommentaireLibre().trim());
            } else {
                sb.append("RAS.");
            }
            sb.append("\n");
        }
        rapport.setObservationsGenerales(sb.toString());
    }

    private void envoyerNotificationMedecin(RapportHebdomadaire rapport, Patient patient) {
        if (patient.getSoignant() == null) return;

        Notification n = new Notification();
        n.setDestinataire(patient.getSoignant());
        n.setPatient(patient);
        n.setType("RAPPORT_HEBDOMADAIRE");
        n.setTitre("📊 Rapport hebdomadaire — " + patient.getNomComplet());
        n.setMessage("Le rapport hebdomadaire du " + rapport.getDateDebut() + " au " + rapport.getDateFin() + " a été généré.");
        n.setReferenceId(rapport.getId());
        n.setReferenceType("RAPPORT_HEBDOMADAIRE");

        Notification saved = notificationRepository.save(n);
        notificationWsService.notifyDoctor(saved);
    }

    private Double extraireObservance(String json) {
        try {
            String priseStr = extractSimpleJson(json, "totalPris");
            String prevuStr = extractSimpleJson(json, "totalPrevus");
            if (priseStr.isEmpty() || prevuStr.isEmpty()) return null;
            double p = Double.parseDouble(priseStr);
            double t = Double.parseDouble(prevuStr);
            return (t > 0) ? (p / t * 100.0) : 100.0;
        } catch (Exception e) { return null; }
    }

    private Double extraireObservanceRepas(String json) {
        try {
            String appetit = extractSimpleJson(json, "appetit").toLowerCase();
            if (appetit.isEmpty()) return null;
            if (appetit.contains("bon")) return 100.0;
            if (appetit.contains("moyen")) return 70.0;
            if (appetit.contains("faible")) return 40.0;
            return 80.0;
        } catch (Exception e) { return null; }
    }

    private String extractSimpleJson(String json, String field) {
        if (json == null || json.isEmpty()) return "";
        String key = "\"" + field + "\"";
        int idx = json.indexOf(key);
        if (idx < 0) return "";
        int colonIdx = json.indexOf(":", idx + key.length());
        if (colonIdx < 0) return "";
        int valStart = colonIdx + 1;
        while (valStart < json.length() && (json.charAt(valStart) == ' ' || json.charAt(valStart) == '\t')) valStart++;
        if (valStart >= json.length()) return "";
        char c = json.charAt(valStart);
        if (c == '"') {
            int valEnd = json.indexOf("\"", valStart + 1);
            return valEnd > valStart ? json.substring(valStart + 1, valEnd) : "";
        }
        int valEnd = valStart;
        while (valEnd < json.length() && json.charAt(valEnd) != ',' && json.charAt(valEnd) != ' ' && json.charAt(valEnd) != '}' && json.charAt(valEnd) != '\n') {
            valEnd++;
        }
        return json.substring(valStart, valEnd).trim();
    }
}
