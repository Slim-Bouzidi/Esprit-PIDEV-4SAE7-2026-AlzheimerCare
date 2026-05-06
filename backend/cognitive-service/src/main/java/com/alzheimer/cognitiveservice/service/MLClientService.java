package com.alzheimer.cognitiveservice.service;

import com.alzheimer.cognitiveservice.dto.MLPredictRequest;
import com.alzheimer.cognitiveservice.dto.MLPredictResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * ═══════════════════════════════════════════════════════════════
 *  ML CLIENT SERVICE — Bridge to the Python ML microservice
 * ═══════════════════════════════════════════════════════════════
 *
 * Sends patient feature vectors to the FastAPI prediction service
 * and returns the risk assessment result.
 *
 * The ML service URL is configurable via environment variable:
 *   ML_SERVICE_URL (default: http://localhost:5000)
 *
 * In Docker/K8s, set ML_SERVICE_URL=http://ml-service:5000
 */
@Service
@Slf4j
public class MLClientService {

    private final RestTemplate restTemplate;
    private final String mlServiceUrl;

    public MLClientService(
            RestTemplate restTemplate,
            @Value("${ml.service.url:http://localhost:5000}") String mlServiceUrl) {
        this.restTemplate = restTemplate;
        this.mlServiceUrl = mlServiceUrl;
        log.info("ML Service URL configured: {}", mlServiceUrl);
    }

    /**
     * Send a prediction request to the Python ML service.
     *
     * @param request Contains the ordered feature list.
     * @return Prediction result with class and probability.
     * @throws RuntimeException if the ML service is unreachable or returns an error.
     */
    public MLPredictResponse predict(MLPredictRequest request) {
        String url = mlServiceUrl + "/predict";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MLPredictRequest> entity = new HttpEntity<>(request, headers);

        try {
            log.info("Calling ML service at {} with {} features", url, request.getFeatures().size());

            ResponseEntity<MLPredictResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    MLPredictResponse.class
            );

            MLPredictResponse body = response.getBody();
            log.info("ML prediction received — class: {}, probability: {}",
                    body != null ? body.getPrediction() : "null",
                    body != null ? body.getProbability() : "null");

            return body;

        } catch (ResourceAccessException e) {
            log.error("ML service unreachable at {}: {}", url, e.getMessage());
            throw new RuntimeException("ML prediction service is unavailable. Ensure the service is running.", e);
        } catch (HttpClientErrorException e) {
            log.error("ML service returned error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("ML prediction failed: " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * Check if the ML service is healthy.
     */
    public boolean isHealthy() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(mlServiceUrl + "/health", String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("ML service health check failed: {}", e.getMessage());
            return false;
        }
    }
}
