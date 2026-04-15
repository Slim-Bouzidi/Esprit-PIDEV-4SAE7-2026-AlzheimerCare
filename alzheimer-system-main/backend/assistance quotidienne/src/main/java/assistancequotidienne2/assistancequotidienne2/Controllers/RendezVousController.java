package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.RendezVous;
import assistancequotidienne2.assistancequotidienne2.Entities.StatutRendezVous;
import assistancequotidienne2.assistancequotidienne2.Repositories.RendezVousRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rendez-vous")
public class RendezVousController {

    @Autowired
    private RendezVousRepository rendezVousRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private UserRepository userRepository;

    // CREATE
    @PostMapping
    public ResponseEntity<RendezVous> create(@RequestBody RendezVous rendezVous) {
        // Vérifier patient
        patientRepository.findById(rendezVous.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        
        // Vérifier soignant (optionnel)
        if (rendezVous.getSoignant() != null && rendezVous.getSoignant().getId() != null) {
            userRepository.findById(rendezVous.getSoignant().getId())
                    .orElseThrow(() -> new RuntimeException("Soignant non trouvé"));
        }
        
        return ResponseEntity.ok(rendezVousRepository.save(rendezVous));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<RendezVous>> getAll() {
        return ResponseEntity.ok(rendezVousRepository.findAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<RendezVous> getById(@PathVariable Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        return ResponseEntity.ok(rdv);
    }

    // READ BY PATIENT
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RendezVous>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(rendezVousRepository.findByPatientId(patientId));
    }

    // READ BY SOIGNANT
    @GetMapping("/soignant/{soignantId}")
    public ResponseEntity<List<RendezVous>> getBySoignant(@PathVariable Long soignantId) {
        return ResponseEntity.ok(rendezVousRepository.findBySoignantId(soignantId));
    }

    // READ A VENIR
    @GetMapping("/a-venir")
    public ResponseEntity<List<RendezVous>> getAVenir() {
        return ResponseEntity.ok(
            rendezVousRepository.findByDateHeureAfterAndStatut(
                LocalDateTime.now(), 
                StatutRendezVous.PLANIFIE
            )
        );
    }

    // READ AUJOURD'HUI
    @GetMapping("/aujourdhui")
    public ResponseEntity<List<RendezVous>> getAujourdhui() {
        LocalDateTime debut = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return ResponseEntity.ok(rendezVousRepository.findByDateHeureBetween(debut, fin));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<RendezVous> update(@PathVariable Long id, @RequestBody RendezVous rendezVous) {
        RendezVous existing = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        
        existing.setTitre(rendezVous.getTitre());
        existing.setDescription(rendezVous.getDescription());
        existing.setDateHeure(rendezVous.getDateHeure());
        existing.setDureeMinutes(rendezVous.getDureeMinutes());
        existing.setTypeRdv(rendezVous.getTypeRdv());
        existing.setStatut(rendezVous.getStatut());
        existing.setLieu(rendezVous.getLieu());
        existing.setMotif(rendezVous.getMotif());
        existing.setNotes(rendezVous.getNotes());
        
        return ResponseEntity.ok(rendezVousRepository.save(existing));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rendezVousRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // CHANGER STATUT
    @PatchMapping("/{id}/statut/{statut}")
    public ResponseEntity<RendezVous> changerStatut(@PathVariable Long id, @PathVariable StatutRendezVous statut) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        
        rdv.setStatut(statut);
        return ResponseEntity.ok(rendezVousRepository.save(rdv));
    }

    // GET BY DATE (front-end: /date/{date})
    @GetMapping("/date/{date}")
    public ResponseEntity<List<RendezVous>> getByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime debut = localDate.atStartOfDay();
        LocalDateTime fin = localDate.atTime(23, 59, 59);
        return ResponseEntity.ok(rendezVousRepository.findByDateHeureBetween(debut, fin));
    }

    // GET BY DATE RANGE (front-end: /periode?debut=...&fin=...)
    @GetMapping("/periode")
    public ResponseEntity<List<RendezVous>> getByDateRange(
            @RequestParam String debut, @RequestParam String fin) {
        LocalDateTime dateDebut = LocalDate.parse(debut).atStartOfDay();
        LocalDateTime dateFin = LocalDate.parse(fin).atTime(23, 59, 59);
        return ResponseEntity.ok(rendezVousRepository.findByDateHeureBetween(dateDebut, dateFin));
    }

    // CONFIRMER (front-end: PUT /{id}/confirmer)
    @PutMapping("/{id}/confirmer")
    public ResponseEntity<RendezVous> confirmer(@PathVariable Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        rdv.setStatut(StatutRendezVous.CONFIRME);
        return ResponseEntity.ok(rendezVousRepository.save(rdv));
    }

    // ANNULER (front-end: PUT /{id}/annuler)
    @PutMapping("/{id}/annuler")
    public ResponseEntity<RendezVous> annuler(@PathVariable Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        rdv.setStatut(StatutRendezVous.ANNULE);
        return ResponseEntity.ok(rendezVousRepository.save(rdv));
    }
}
