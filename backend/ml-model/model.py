"""
═══════════════════════════════════════════════════════════════
  Alzheimer Risk Prediction — Model Loader
═══════════════════════════════════════════════════════════════

  Loads model.pkl and exposes predict() for inference.
  Uses pandas DataFrame for inference because the sklearn
  ColumnTransformer was trained with named columns.
  No dataset needed — only model.pkl.
"""

import os
import logging
import numpy as np
import pandas as pd
import joblib

logger = logging.getLogger(__name__)

# ── Resolve model path ───────────────────────────────────────
MODEL_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "model.pkl")

# ── Load once at import ──────────────────────────────────────
try:
    pipeline = joblib.load(MODEL_PATH)
    logger.info("Model loaded from %s", MODEL_PATH)
except FileNotFoundError:
    pipeline = None
    logger.error("model.pkl not found at %s", MODEL_PATH)
except Exception as e:
    pipeline = None
    logger.error("Failed to load model: %s", e)

# ── Feature columns (must match training order exactly) ──────
FEATURE_COLUMNS = [
    "Age", "Gender", "Ethnicity", "EducationLevel",
    "BMI", "Smoking", "AlcoholConsumption", "PhysicalActivity",
    "DietQuality", "SleepQuality", "FamilyHistoryAlzheimers",
    "CardiovascularDisease", "Diabetes", "Depression",
    "HeadInjury", "Hypertension", "SystolicBP", "DiastolicBP",
    "CholesterolTotal", "CholesterolLDL", "CholesterolHDL",
    "CholesterolTriglycerides", "MMSE", "FunctionalAssessment",
    "MemoryComplaints", "BehavioralProblems", "ADL",
    "Confusion", "Disorientation", "PersonalityChanges",
    "DifficultyCompletingTasks", "Forgetfulness",
]


def predict(features: list) -> dict:
    """
    Run inference on a single sample.

    Args:
        features: list of 32 numeric values in FEATURE_COLUMNS order.

    Returns:
        dict with "prediction" (int) and "probability" (float or None).
    """
    if pipeline is None:
        raise RuntimeError("Model is not loaded. Ensure model.pkl exists.")

    if len(features) != len(FEATURE_COLUMNS):
        raise ValueError(
            f"Expected {len(FEATURE_COLUMNS)} features, got {len(features)}"
        )

    # ColumnTransformer was trained with named columns (DataFrame),
    # so we must pass a DataFrame at inference time too.
    df = pd.DataFrame([features], columns=FEATURE_COLUMNS)

    prediction = int(pipeline.predict(df)[0])

    probability = None
    if hasattr(pipeline, "predict_proba"):
        try:
            proba = pipeline.predict_proba(df)[0]
            probability = float(proba[1]) if len(proba) > 1 else float(proba[0])
        except Exception as e:
            logger.warning("predict_proba failed: %s", e)

    return {
        "prediction": prediction,
        "probability": probability,
    }