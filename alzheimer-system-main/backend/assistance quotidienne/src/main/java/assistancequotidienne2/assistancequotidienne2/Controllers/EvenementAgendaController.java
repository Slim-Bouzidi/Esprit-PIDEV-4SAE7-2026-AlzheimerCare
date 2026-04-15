package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.*;
import assistancequotidienne2.assistancequotidienne2.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/agenda")
public class EvenementAgendaController {

    @Autowired
    private EvenementAgendaRepository agendaRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TraitementRepository traitementRepository;

    @Autowired
    private RendezVousRepository rendezVousRepository;

    // CREATE
    @PostMapping
    public ResponseEntity<EvenementAgenda> create(@RequestBody EvenementAgenda event) {
        if (event.getPatient() != null && event.getPatient().getId() != null) {
            patientRepository.findById(event.getPatient().getId())
                    .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        }
        return ResponseEntity.ok(agendaRepository.save(event));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<EvenementAgenda>> getAll() {
        return ResponseEntity.ok(agendaRepository.findAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<EvenementAgenda> getById(@PathVariable Long id) {
        EvenementAgenda event = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        return ResponseEntity.ok(event);
    }

    // READ BY PATIENT
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<EvenementAgenda>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(agendaRepository.findByPatientId(patientId));
    }

    // READ BY DATE
    @GetMapping("/date/{date}")
    public ResponseEntity<List<EvenementAgenda>> getByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(agendaRepository.findByDateEvenement(localDate));
    }

    // READ BY WEEK (date range)
    @GetMapping("/semaine")
    public ResponseEntity<List<EvenementAgenda>> getBySemaine(
            @RequestParam String debut,
            @RequestParam String fin) {
        LocalDate dateDebut = LocalDate.parse(debut);
        LocalDate dateFin = LocalDate.parse(fin);
        return ResponseEntity.ok(agendaRepository.findByDateEvenementBetween(dateDebut, dateFin));
    }

    // READ BY PATIENT AND DATE RANGE
    @GetMapping("/patient/{patientId}/semaine")
    public ResponseEntity<List<EvenementAgenda>> getByPatientAndSemaine(
            @PathVariable Long patientId,
            @RequestParam String debut,
            @RequestParam String fin) {
        LocalDate dateDebut = LocalDate.parse(debut);
        LocalDate dateFin = LocalDate.parse(fin);
        return ResponseEntity.ok(agendaRepository.findByPatientIdAndDateEvenementBetween(patientId, dateDebut, dateFin));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<EvenementAgenda> update(@PathVariable Long id, @RequestBody EvenementAgenda event) {
        EvenementAgenda existing = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        existing.setType(event.getType());
        existing.setHeure(event.getHeure());
        existing.setTitre(event.getTitre());
        existing.setDetail(event.getDetail());
        existing.setPatientNom(event.getPatientNom());
        existing.setStatut(event.getStatut());
        existing.setDateEvenement(event.getDateEvenement());

        return ResponseEntity.ok(agendaRepository.save(existing));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agendaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // UPDATE STATUS
    @PatchMapping("/{id}/statut")
    public ResponseEntity<EvenementAgenda> updateStatut(
            @PathVariable Long id,
            @RequestParam String statut) {
        EvenementAgenda event = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
        event.setStatut(statut);
        return ResponseEntity.ok(agendaRepository.save(event));
    }

    /**
     * GENERER les événements agenda automatiquement à partir de :
     * - Traitements actifs (médicaments → événements matin/midi/soir/coucher)
     * - RendezVous planifiés/confirmés (rdv → événements rendez_vous)
     * 
     * Ne crée PAS de doublons : vérifie si un événement avec le même titre+patient+date+heure existe déjà.
     */
    @PostMapping("/generer")
    public ResponseEntity<List<EvenementAgenda>> genererEvenements(
            @RequestParam String debut,
            @RequestParam String fin) {

        LocalDate dateDebut = LocalDate.parse(debut);
        LocalDate dateFin = LocalDate.parse(fin);
        List<EvenementAgenda> created = new ArrayList<>();

        // Récupérer tous les patients actifs
        List<Patient> patients = patientRepository.findByActifTrue();

        for (Patient patient : patients) {
            // === 1. Générer les événements MÉDICAMENTS depuis les Traitements actifs ===
            List<Traitement> traitements = traitementRepository.findByPatientIdAndActifTrue(patient.getId());
            for (Traitement t : traitements) {
                // Vérifier que le traitement est dans la période
                for (LocalDate date = dateDebut; !date.isAfter(dateFin); date = date.plusDays(1)) {
                    if (t.getDateDebut() != null && date.isBefore(t.getDateDebut())) continue;
                    if (t.getDateFin() != null && date.isAfter(t.getDateFin())) continue;

                    String nomMed = t.getNomMedicament() + (t.getDosage() != null ? " " + t.getDosage() : "");

                    if (Boolean.TRUE.equals(t.getMomentMatin())) {
                        created.addAll(creerSiAbsent(patient, "medicament", "08:00", nomMed, "Prise du matin", date));
                    }
                    if (Boolean.TRUE.equals(t.getMomentMidi())) {
                        created.addAll(creerSiAbsent(patient, "medicament", "12:00", nomMed, "Prise du midi", date));
                    }
                    if (Boolean.TRUE.equals(t.getMomentSoir())) {
                        created.addAll(creerSiAbsent(patient, "medicament", "18:00", nomMed, "Prise du soir", date));
                    }
                    if (Boolean.TRUE.equals(t.getMomentCoucher())) {
                        created.addAll(creerSiAbsent(patient, "medicament", "21:00", nomMed, "Prise du coucher", date));
                    }
                    if (t.getHeurePersonnalisee() != null) {
                        String heure = t.getHeurePersonnalisee().format(DateTimeFormatter.ofPattern("HH:mm"));
                        created.addAll(creerSiAbsent(patient, "medicament", heure, nomMed, "Prise personnalisée", date));
                    }
                }
            }

            // === 2. Générer les événements depuis les RendezVous ===
            List<RendezVous> rendezVousList = rendezVousRepository.findByPatientId(patient.getId());
            for (RendezVous rdv : rendezVousList) {
                if (rdv.getDateHeure() == null) continue;
                LocalDate dateRdv = rdv.getDateHeure().toLocalDate();
                if (dateRdv.isBefore(dateDebut) || dateRdv.isAfter(dateFin)) continue;

                String heure = rdv.getDateHeure().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                String detail = (rdv.getLieu() != null ? rdv.getLieu() : "");
                if (rdv.getMotif() != null && !rdv.getMotif().isEmpty()) {
                    detail += (detail.isEmpty() ? "" : " - ") + rdv.getMotif();
                }

                String statutRdv = "en_attente";
                if (rdv.getStatut() != null) {
                    if (rdv.getStatut().name().equals("TERMINE")) statutRdv = "fait";
                    else if (rdv.getStatut().name().equals("ANNULE")) continue; // Skip cancelled
                }

                created.addAll(creerSiAbsent(patient, "rendez_vous", heure, rdv.getTitre(), detail, dateRdv, statutRdv));
            }
        }

        // === 3. Ajouter des événements REPAS standards pour chaque patient/jour si absents ===
        for (Patient patient : patients) {
            for (LocalDate date = dateDebut; !date.isAfter(dateFin); date = date.plusDays(1)) {
                created.addAll(creerSiAbsent(patient, "repas", "08:00", "Petit-déjeuner", patient.getNomComplet() != null ? patient.getNomComplet() : "", date));
                created.addAll(creerSiAbsent(patient, "repas", "12:30", "Déjeuner", patient.getNomComplet() != null ? patient.getNomComplet() : "", date));
                created.addAll(creerSiAbsent(patient, "repas", "19:00", "Dîner", patient.getNomComplet() != null ? patient.getNomComplet() : "", date));
            }
        }

        return ResponseEntity.ok(created);
    }

    // ===== Helper methods =====

    private List<EvenementAgenda> creerSiAbsent(Patient patient, String type, String heure, String titre, String detail, LocalDate date) {
        return creerSiAbsent(patient, type, heure, titre, detail, date, "en_attente");
    }

    private List<EvenementAgenda> creerSiAbsent(Patient patient, String type, String heure, String titre, String detail, LocalDate date, String statut) {
        List<EvenementAgenda> result = new ArrayList<>();
        // Vérifier si un événement similaire existe déjà
        List<EvenementAgenda> existing = agendaRepository.findByPatientIdAndDateEvenement(patient.getId(), date);
        boolean alreadyExists = existing.stream().anyMatch(e ->
                e.getTitre() != null && e.getTitre().equals(titre) &&
                e.getHeure() != null && e.getHeure().equals(heure) &&
                e.getType() != null && e.getType().equals(type)
        );

        if (!alreadyExists) {
            EvenementAgenda event = new EvenementAgenda();
            event.setPatient(patient);
            event.setPatientNom(patient.getNomComplet());
            event.setType(type);
            event.setHeure(heure);
            event.setTitre(titre);
            event.setDetail(detail);
            event.setDateEvenement(date);
            event.setStatut(statut);
            result.add(agendaRepository.save(event));
        }
        return result;
    }
}
