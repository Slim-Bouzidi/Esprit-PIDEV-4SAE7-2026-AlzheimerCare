package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.*;
import assistancequotidienne2.assistancequotidienne2.Repositories.*;
import assistancequotidienne2.assistancequotidienne2.Services.FichePdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/fiches")
public class FicheTransmissionController {

    @Autowired
    private FicheTransmissionRepository ficheRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private RapportRepository rapportRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FichePdfService fichePdfService;

    @Autowired
    private assistancequotidienne2.assistancequotidienne2.Services.NotificationWsService notificationWsService;

    // CREATE — save fiche as brouillon (no auto-send)
    @PostMapping
    public ResponseEntity<FicheTransmission> create(@RequestBody FicheTransmission fiche) {
        Patient patient = null;
        if (fiche.getPatient() != null && fiche.getPatient().getId() != null) {
            patient = patientRepository.findById(fiche.getPatient().getId())
                    .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
            fiche.setPatient(patient);
        }

        // Business rule: one fiche per patient per day.
        LocalDate dateFiche = fiche.getDateFiche() != null ? fiche.getDateFiche() : LocalDate.now();
        fiche.setDateFiche(dateFiche);
        if (patient != null) {
            List<FicheTransmission> existing = ficheRepository.findByPatientIdAndDateFiche(patient.getId(), dateFiche);
            if (!existing.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
        }

        // Force statut to brouillon on creation
        fiche.setStatut("brouillon");
        fiche.setDateEnvoi(null);

        FicheTransmission saved = ficheRepository.save(fiche);

        // NOTE: Rapport and Notification are created only when fiche is sent via /envoyer endpoint

        return ResponseEntity.ok(saved);
    }

    // GENERATE PDF for a fiche
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        FicheTransmission fiche = ficheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fiche non trouvée"));

        byte[] pdfBytes = fichePdfService.genererPdf(fiche);

        String patientNom = "patient";
        if (fiche.getPatientInfoJson() != null) {
            // Extract patient name for filename
            String prenom = extractSimpleJson(fiche.getPatientInfoJson(), "prenom");
            String nom = extractSimpleJson(fiche.getPatientInfoJson(), "nom");
            patientNom = (prenom + "_" + nom).replaceAll("[^a-zA-ZÀ-ÿ_]", "");
        }
        String filename = "FT-" + String.format("%05d", id) + "_" + patientNom + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<FicheTransmission>> getAll() {
        List<FicheTransmission> fiches = ficheRepository.findAll();
        // Enrich with patient and soignant names for frontend display
        for (FicheTransmission fiche : fiches) {
            if (fiche.getPatient() != null) {
                fiche.setPatientNom(fiche.getPatient().getNomComplet());
            }
            if (fiche.getSoignant() != null) {
                fiche.setSoignantNom(fiche.getSoignant().getNom());
            }
        }
        return ResponseEntity.ok(fiches);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<FicheTransmission> getById(@PathVariable Long id) {
        FicheTransmission fiche = ficheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fiche non trouvée"));
        return ResponseEntity.ok(fiche);
    }

    // READ BY PATIENT
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<FicheTransmission>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ficheRepository.findByPatientIdOrderByDateCreationDesc(patientId));
    }

    // READ BY PATIENT AND DATE
    @GetMapping("/patient/{patientId}/date/{date}")
    public ResponseEntity<List<FicheTransmission>> getByPatientAndDate(
            @PathVariable Long patientId, @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(ficheRepository.findByPatientIdAndDateFiche(patientId, localDate));
    }

    // READ BY PATIENT AND DATE RANGE
    @GetMapping("/patient/{patientId}/periode")
    public ResponseEntity<List<FicheTransmission>> getByPatientAndPeriode(
            @PathVariable Long patientId,
            @RequestParam String debut,
            @RequestParam String fin) {
        LocalDate dateDebut = LocalDate.parse(debut);
        LocalDate dateFin = LocalDate.parse(fin);
        return ResponseEntity.ok(ficheRepository.findByPatientIdAndDateFicheBetween(patientId, dateDebut, dateFin));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<FicheTransmission> update(@PathVariable Long id, @RequestBody FicheTransmission fiche) {
        FicheTransmission existing = ficheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fiche non trouvée"));

        existing.setStatut(fiche.getStatut());
        existing.setPatientInfoJson(fiche.getPatientInfoJson());
        existing.setSoignantInfoJson(fiche.getSoignantInfoJson());
        existing.setObservanceMedicamentsJson(fiche.getObservanceMedicamentsJson());
        existing.setAlimentationJson(fiche.getAlimentationJson());
        existing.setVieSocialeJson(fiche.getVieSocialeJson());
        existing.setSuiviDirectivesJson(fiche.getSuiviDirectivesJson());
        existing.setSignatureSoignant(fiche.getSignatureSoignant());
        existing.setCommentaireLibre(fiche.getCommentaireLibre());

        return ResponseEntity.ok(ficheRepository.save(existing));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ficheRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // MARQUER COMME ENVOYE — sends notification to the doctor
    @PatchMapping("/{id}/envoyer")
    public ResponseEntity<FicheTransmission> marquerEnvoye(@PathVariable Long id) {
        FicheTransmission fiche = ficheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fiche non trouvée"));

        // Verify fiche is signed
        if (fiche.getSignatureSoignant() == null || !fiche.getSignatureSoignant()) {
            return ResponseEntity.badRequest().build();
        }

        // Already sent?
        if ("envoye".equals(fiche.getStatut())) {
            return ResponseEntity.ok(fiche);
        }

        fiche.marquerEnvoye();
        FicheTransmission saved = ficheRepository.save(fiche);

        // Create Rapport when sending
        try {
            Patient patient = fiche.getPatient();
            
            Rapport rapport = new Rapport();
            rapport.setPatient(patient);
            rapport.setTypeRapport(TypeRapport.HEBDOMADAIRE);
            rapport.setPeriodeDebut(saved.getDateFiche());
            rapport.setPeriodeFin(saved.getDateFiche());

            String patientNom = patient != null ? patient.getNomComplet() : "Patient";
            String dateStr = saved.getDateFiche() != null
                    ? saved.getDateFiche().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    : LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            rapport.setTitre("Fiche de Transmission — " + patientNom + " — " + dateStr);
            rapport.setContenuTexte(buildContenuFromFiche(saved));
            rapport.setStatut(StatutRapport.ENVOYE);
            rapport.setFormatExport(FormatRapport.PDF);
            rapport.setCheminFichier("FT-" + String.format("%05d", saved.getId()) + ".pdf");

            // Extract observance data from JSON
            extractIndicatorsFromFiche(saved, rapport);

            rapportRepository.save(rapport);
        } catch (Exception e) {
            System.err.println("Erreur création rapport: " + e.getMessage());
        }

        // Create notification for the doctor
        try {
            Patient patient = fiche.getPatient();
            if (patient == null) {
                return ResponseEntity.ok(saved);
            }

            // Determine destinataire (doctor) even if patient has no assigned soignant
            User destinataire = patient.getSoignant();
            if (destinataire == null) {
                List<User> doctors = userRepository.findByRole(Role.DOCTEUR);
                if (doctors != null && !doctors.isEmpty()) {
                    destinataire = doctors.get(0);
                }
            }

            if (destinataire != null) {
                String patientNom = patient.getNomComplet() != null ? patient.getNomComplet() : "Patient";
                String dateStr = fiche.getDateFiche() != null
                        ? fiche.getDateFiche().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                Notification notif = new Notification();
                notif.setDestinataire(destinataire);
                notif.setPatient(patient);
                notif.setType("FICHE_ENVOYEE");
                notif.setTitre("Rapport hebdomadaire envoyé — " + patientNom);
                notif.setMessage("Le soignant a validé et envoyé la fiche de transmission de "
                        + patientNom + " du " + dateStr
                        + ". Le rapport PDF est disponible dans votre espace Rapports patients.");
                notif.setReferenceId(saved.getId());
                notif.setReferenceType("FICHE_TRANSMISSION");
                Notification savedNotif = notificationRepository.save(notif);

                // Send real-time notification via WebSocket
                notificationWsService.notifyDoctor(savedNotif);
            } else {
                System.err.println("Aucun docteur trouvé pour recevoir la notification (patient sans soignant assigné).");
            }
        } catch (Exception e) {
            System.err.println("Erreur notification envoi médecin: " + e.getMessage());
        }

        return ResponseEntity.ok(saved);
    }

    // === Helper methods for auto-rapport generation ===

    private String buildContenuFromFiche(FicheTransmission fiche) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== FICHE DE TRANSMISSION ===\n\n");

        if (fiche.getObservanceMedicamentsJson() != null) {
            sb.append("OBSERVANCE MÉDICAMENTEUSE:\n");
            String totalPris = extractSimpleJson(fiche.getObservanceMedicamentsJson(), "totalPris");
            String totalPrevus = extractSimpleJson(fiche.getObservanceMedicamentsJson(), "totalPrevus");
            sb.append("Médicaments pris: ").append(totalPris).append("/").append(totalPrevus).append("\n\n");
        }

        if (fiche.getAlimentationJson() != null) {
            sb.append("ALIMENTATION:\n");
            sb.append("Appétit: ").append(extractSimpleJson(fiche.getAlimentationJson(), "appetit")).append("\n");
            sb.append("Hydratation: ").append(extractSimpleJson(fiche.getAlimentationJson(), "hydratation")).append("\n\n");
        }

        if (fiche.getVieSocialeJson() != null) {
            sb.append("VIE SOCIALE:\n");
            sb.append("Interaction: ").append(extractSimpleJson(fiche.getVieSocialeJson(), "interaction")).append("\n");
            sb.append("Sommeil: ").append(extractSimpleJson(fiche.getVieSocialeJson(), "sommeil")).append("\n\n");
        }

        if (fiche.getCommentaireLibre() != null && !fiche.getCommentaireLibre().isEmpty()) {
            sb.append("COMMENTAIRES:\n").append(fiche.getCommentaireLibre()).append("\n");
        }

        return sb.toString();
    }

    private void extractIndicatorsFromFiche(FicheTransmission fiche, Rapport rapport) {
        try {
            if (fiche.getObservanceMedicamentsJson() != null) {
                String totalPris = extractSimpleJson(fiche.getObservanceMedicamentsJson(), "totalPris");
                String totalPrevus = extractSimpleJson(fiche.getObservanceMedicamentsJson(), "totalPrevus");
                if (!totalPris.isEmpty() && !totalPrevus.isEmpty()) {
                    double pris = Double.parseDouble(totalPris);
                    double prevus = Double.parseDouble(totalPrevus);
                    if (prevus > 0) {
                        rapport.setTauxObservance(Math.round(pris / prevus * 100.0 * 10.0) / 10.0);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

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
