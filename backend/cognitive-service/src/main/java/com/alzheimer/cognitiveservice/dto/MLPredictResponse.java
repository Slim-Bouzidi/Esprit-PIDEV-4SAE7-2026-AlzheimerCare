package com.alzheimer.cognitiveservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO from the Python ML service.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MLPredictResponse {
    /**
     * Predicted class: 0 = low risk, 1 = high risk.
     */
    private Integer prediction;

    /**
     * Probability of the positive class (high risk).
     * May be null if the model does not support predict_proba.
     */
    private Double probability;
}
