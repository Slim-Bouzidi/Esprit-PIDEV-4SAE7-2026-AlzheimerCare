package com.alzheimer.cognitiveservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for ML prediction.
 *
 * The features list must contain exactly 32 numeric values in this order:
 * Age, Gender, Ethnicity, EducationLevel, BMI, Smoking, AlcoholConsumption,
 * PhysicalActivity, DietQuality, SleepQuality, FamilyHistoryAlzheimers,
 * CardiovascularDisease, Diabetes, Depression, HeadInjury, Hypertension,
 * SystolicBP, DiastolicBP, CholesterolTotal, CholesterolLDL, CholesterolHDL,
 * CholesterolTriglycerides, MMSE, FunctionalAssessment, MemoryComplaints,
 * BehavioralProblems, ADL, Confusion, Disorientation, PersonalityChanges,
 * DifficultyCompletingTasks, Forgetfulness
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MLPredictRequest {
    private List<Double> features;
}
