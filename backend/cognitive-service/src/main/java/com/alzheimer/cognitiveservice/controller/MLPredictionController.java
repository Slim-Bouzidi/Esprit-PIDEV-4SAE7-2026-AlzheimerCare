package com.alzheimer.cognitiveservice.controller;

import com.alzheimer.cognitiveservice.dto.MLPredictRequest;
import com.alzheimer.cognitiveservice.dto.MLPredictResponse;
import com.alzheimer.cognitiveservice.service.MLClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════════
 *  ML PREDICTION CONTROLLER — Alzheimer Risk Assessment
 * ═══════════════════════════════════════════════════════════════
 *
 * Forwards prediction requests from the frontend / API gateway
 * to the Python FastAPI ML microservice.
 *
 * Endpoints:
 *   POST /api/ml/predict   → Run prediction
 *   GET  /api/ml/health    → Check ML service status
 */
@RestController
@RequestMapping("/api/ml")
@RequiredArgsConstructor
@Slf4j
public class MLPredictionController {

    private final MLClientService mlClientService;

    /**
     * Forward a prediction request to the Python ML service.
     *
     * Request body:
     * {
     *   "features": [72, "Homme", "Bac+5", "Modérée", ...]
     * }
     *
     * Response:
     * {
     *   "prediction": 1,
     *   "probability": 0.87
     * }
     */
    @PostMapping("/predict")
    public ResponseEntity<MLPredictResponse> predict(@RequestBody MLPredictRequest request) {
        log.info("ML prediction request received with {} features", request.getFeatures().size());

        try {
            MLPredictResponse response = mlClientService.predict(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Prediction failed: {}", e.getMessage());
            return ResponseEntity.status(503).body(
                    MLPredictResponse.builder()
                            .prediction(null)
                            .probability(null)
                            .build()
            );
        }
    }

    /**
     * Health check endpoint for the ML service.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        boolean healthy = mlClientService.isHealthy();
        return ResponseEntity.ok(Map.of(
                "service", "cognitive-service",
                "mlServiceStatus", healthy ? "UP" : "DOWN",
                "mlServiceHealthy", healthy
        ));
    }
}
