package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Services.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insights")
public class InsightController {

    @Autowired
    private InsightService insightService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<InsightService.InsightDto>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(insightService.getInsightsByPatient(patientId));
    }

    @PostMapping("/interaction")
    public ResponseEntity<Void> recordInteraction(
            @RequestParam Long patientId,
            @RequestParam String type,
            @RequestParam(required = false) String details) {
        insightService.recordInteraction(patientId, type, details);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/analyze/all")
    public ResponseEntity<Void> analyzeAll() {
        insightService.triggerGlobalAnalysis();
        return ResponseEntity.ok().build();
    }
}
