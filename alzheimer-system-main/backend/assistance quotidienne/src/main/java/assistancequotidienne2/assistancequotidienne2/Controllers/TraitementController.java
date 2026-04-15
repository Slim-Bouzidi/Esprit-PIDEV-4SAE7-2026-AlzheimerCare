package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.Traitement;
import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Repositories.TraitementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/traitements")
public class TraitementController {

    @Autowired
    private TraitementRepository traitementRepository;
    
    // CREATE
    @PostMapping
    public ResponseEntity<Traitement> create(@RequestBody Traitement traitement) {
        Traitement saved = traitementRepository.save(traitement);
        return ResponseEntity.ok(saved);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Traitement>> getAll() {
        return ResponseEntity.ok(traitementRepository.findAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Traitement> getById(@PathVariable Long id) {
        Traitement traitement = traitementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Traitement non trouvé"));
        return ResponseEntity.ok(traitement);
    }

    // READ BY PATIENT
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Traitement>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(traitementRepository.findByPatientIdAndActifTrue(patientId));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Traitement> update(@PathVariable Long id, @RequestBody Traitement traitement) {
        Traitement existing = traitementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Traitement non trouvé"));
        
        existing.setNomMedicament(traitement.getNomMedicament());
        existing.setDosage(traitement.getDosage());
        existing.setFrequence(traitement.getFrequence());
        existing.setMomentMatin(traitement.getMomentMatin());
        existing.setMomentMidi(traitement.getMomentMidi());
        existing.setMomentSoir(traitement.getMomentSoir());
        existing.setMomentCoucher(traitement.getMomentCoucher());
        existing.setDateFin(traitement.getDateFin());
        existing.setActif(traitement.getActif());
        
        return ResponseEntity.ok(traitementRepository.save(existing));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        traitementRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ENREGISTRER UNE PRISE
    @PostMapping("/{id}/enregistrer-prise")
    public ResponseEntity<Traitement> enregistrerPrise(@PathVariable Long id) {
        Traitement traitement = traitementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Traitement non trouvé"));
        
        traitement.enregistrerPrise();
        return ResponseEntity.ok(traitementRepository.save(traitement));
    }
}
