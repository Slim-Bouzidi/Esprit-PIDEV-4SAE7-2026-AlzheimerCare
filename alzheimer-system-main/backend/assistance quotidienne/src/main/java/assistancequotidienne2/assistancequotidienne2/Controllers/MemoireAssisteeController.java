package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.MemoireAssistee;
import assistancequotidienne2.assistancequotidienne2.Entities.Patient;
import assistancequotidienne2.assistancequotidienne2.Repositories.MemoireAssisteeRepository;
import assistancequotidienne2.assistancequotidienne2.Repositories.PatientRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
public class MemoireAssisteeController {

    private final MemoireAssisteeRepository memoireAssisteeRepository;
    private final PatientRepository patientRepository;
    private final ObjectMapper objectMapper;

    public MemoireAssisteeController(
            MemoireAssisteeRepository memoireAssisteeRepository,
            PatientRepository patientRepository,
            ObjectMapper objectMapper
    ) {
        this.memoireAssisteeRepository = memoireAssisteeRepository;
        this.patientRepository = patientRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/memoires-assistees")
    public ResponseEntity<List<MemoireAssisteeDto>> getAllMemoires() {
        List<MemoireAssisteeDto> memoires = memoireAssisteeRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(memoires);
    }

    @GetMapping("/{patientId}/memoire-assistee")
    public ResponseEntity<MemoireAssisteeDto> getByPatient(@PathVariable Long patientId) {
        MemoireAssistee memoire = memoireAssisteeRepository.findByPatientId(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Aucune fiche mémoire trouvée pour le patient " + patientId));
        return ResponseEntity.ok(toDto(memoire));
    }

    @PutMapping("/{patientId}/memoire-assistee")
    public ResponseEntity<MemoireAssisteeDto> saveForPatient(
            @PathVariable Long patientId,
            @RequestBody MemoireAssisteeDto body
    ) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé avec l'id: " + patientId));

        MemoireAssistee memoire = memoireAssisteeRepository.findByPatientId(patientId)
                .orElseGet(MemoireAssistee::new);

        memoire.setPatient(patient);
        memoire.setAdresse(body.getAdresse());
        memoire.setConjoint(body.getConjoint());
        memoire.setInfosCles(body.getInfosCles());
        memoire.setPhotosJson(writePhotos(body.getPhotos()));

        return ResponseEntity.ok(toDto(memoireAssisteeRepository.save(memoire)));
    }

    private MemoireAssisteeDto toDto(MemoireAssistee memoire) {
        MemoireAssisteeDto dto = new MemoireAssisteeDto();
        dto.setPatientId(memoire.getPatient().getId());
        dto.setPatientName(memoire.getPatient().getNomComplet());
        dto.setAdresse(memoire.getAdresse());
        dto.setConjoint(memoire.getConjoint());
        dto.setInfosCles(memoire.getInfosCles());
        dto.setPhotos(readPhotos(memoire.getPhotosJson()));
        return dto;
    }

    private List<String> readPhotos(String photosJson) {
        if (photosJson == null || photosJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(photosJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Photos mémoire assistée invalides");
        }
    }

    private String writePhotos(List<String> photos) {
        try {
            return objectMapper.writeValueAsString(photos == null ? Collections.emptyList() : photos);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Impossible de sauvegarder les photos mémoire assistée");
        }
    }

    public static class MemoireAssisteeDto {
        private Long patientId;
        private String patientName;
        private String adresse;
        private String conjoint;
        private String infosCles;
        private List<String> photos;

        public Long getPatientId() { return patientId; }
        public void setPatientId(Long patientId) { this.patientId = patientId; }

        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }

        public String getAdresse() { return adresse; }
        public void setAdresse(String adresse) { this.adresse = adresse; }

        public String getConjoint() { return conjoint; }
        public void setConjoint(String conjoint) { this.conjoint = conjoint; }

        public String getInfosCles() { return infosCles; }
        public void setInfosCles(String infosCles) { this.infosCles = infosCles; }

        public List<String> getPhotos() { return photos; }
        public void setPhotos(List<String> photos) { this.photos = photos; }
    }
}
