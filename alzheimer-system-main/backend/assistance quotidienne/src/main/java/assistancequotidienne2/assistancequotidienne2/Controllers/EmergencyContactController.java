package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.DTOs.EmergencyContactDTO;
import assistancequotidienne2.assistancequotidienne2.Entities.EmergencyContact;
import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Repositories.EmergencyContactRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/emergencyContact")
public class EmergencyContactController {

    @Autowired
    private EmergencyContactRepository emergencyContactRepository;

    @Autowired
    private PatientRepository patientRepository;

    @PostMapping("/addEmergencyContact")
    public ResponseEntity<EmergencyContact> addEmergencyContact(@Valid @RequestBody EmergencyContactDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé avec l'ID: " + dto.getPatientId()));

        EmergencyContact contact = new EmergencyContact();
        contact.setFullName(dto.getFullName());
        contact.setRelationship(dto.getRelationship());
        contact.setPhone(dto.getPhone());
        contact.setEmail(dto.getEmail());
        contact.setPatient(patient);

        return ResponseEntity.ok(emergencyContactRepository.save(contact));
    }

    @GetMapping("/allEmergencyContact")
    public ResponseEntity<List<EmergencyContact>> getAllEmergencyContacts() {
        return ResponseEntity.ok(emergencyContactRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyContact> getEmergencyContact(@PathVariable Long id) {
        return ResponseEntity.ok(emergencyContactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact d'urgence non trouvé avec l'ID: " + id)));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<EmergencyContact>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(emergencyContactRepository.findByPatientId(patientId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEmergencyContact(@PathVariable Long id) {
        try {
            if (!emergencyContactRepository.existsById(id)) {
                return ResponseEntity.status(404).body("Contact d'urgence non trouvé avec l'ID: " + id);
            }
            emergencyContactRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de la suppression du contact d'urgence: " + e.getMessage());
        }
    }
}
