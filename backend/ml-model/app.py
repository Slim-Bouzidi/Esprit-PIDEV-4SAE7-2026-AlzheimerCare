"""
═══════════════════════════════════════════════════════════════
  Alzheimer Risk Prediction — FastAPI Service
═══════════════════════════════════════════════════════════════

  POST /predict   → Run inference
  GET  /health    → Liveness probe

  Start: uvicorn app:app --host 0.0.0.0 --port 5000
"""

import logging
from typing import List, Optional

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

from model import predict, pipeline, FEATURE_COLUMNS

# ── Logging ──────────────────────────────────────────────────
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s — %(message)s",
)
logger = logging.getLogger(__name__)

# ── App ──────────────────────────────────────────────────────
app = FastAPI(
    title="Alzheimer Risk Prediction API",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


# ── Schemas ──────────────────────────────────────────────────
class PredictRequest(BaseModel):
    features: List[float] = Field(
        ...,
        description=f"List of {len(FEATURE_COLUMNS)} numeric feature values",
        min_length=len(FEATURE_COLUMNS),
        max_length=len(FEATURE_COLUMNS),
    )

    class Config:
        json_schema_extra = {
            "example": {
                "features": [
                    73, 0, 0, 2,
                    22.93, 0, 13.30, 6.33,
                    1.35, 9.03, 0,
                    0, 1, 1,
                    0, 0, 142, 72,
                    242.37, 56.15, 33.68,
                    162.19, 21.46, 6.52,
                    0, 0, 1.73,
                    0, 0, 0,
                    1, 0,
                ]
            }
        }


class PredictResponse(BaseModel):
    prediction: int
    probability: Optional[float] = None


class HealthResponse(BaseModel):
    status: str
    model_loaded: bool
    expected_features: int
    feature_columns: List[str]


import mysql.connector
from mysql.connector import errorcode

# --- MySQL Configuration (XAMPP) ---
db_config = {
    'user': 'root',
    'password': '',
    'host': '127.0.0.1',
}

def init_db():
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()
        cursor.execute("CREATE DATABASE IF NOT EXISTS alzheimer_ml")
        cursor.execute("USE alzheimer_ml")
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS predictions_log (
                id INT AUTO_INCREMENT PRIMARY KEY,
                age DOUBLE,
                mmse DOUBLE,
                adl DOUBLE,
                prediction INT,
                probability DOUBLE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """)
        conn.commit()
        cursor.close()
        conn.close()
        logger.info("✅ MySQL Database 'alzheimer_ml' initialized")
    except mysql.connector.Error as err:
        logger.error(f"❌ MySQL Error: {err}")

# Initialize DB on startup
init_db()

# ── Endpoints ────────────────────────────────────────────────
@app.post("/predict", response_model=PredictResponse)
async def predict_endpoint(request: PredictRequest):
    """Run Alzheimer risk prediction on a single sample."""
    try:
        result = predict(request.features)
        
        # Log to MySQL
        try:
            conn = mysql.connector.connect(database='alzheimer_ml', **db_config)
            cursor = conn.cursor()
            query = "INSERT INTO predictions_log (age, mmse, adl, prediction, probability) VALUES (%s, %s, %s, %s, %s)"
            # Mapping: Age(0), MMSE(22), ADL(26)
            values = (request.features[0], request.features[22], request.features[26], int(result["prediction"]), float(result["probability"]))
            cursor.execute(query, values)
            conn.commit()
            cursor.close()
            conn.close()
            logger.info("✅ Prediction logged to MySQL")
        except Exception as db_err:
            logger.warning(f"⚠️ Failed to log to MySQL: {db_err}")

        logger.info("Prediction: %s | Prob: %s", result["prediction"], result["probability"])
        return PredictResponse(**result)
    except RuntimeError as e:
        raise HTTPException(status_code=503, detail=str(e))
    except Exception as e:
        logger.error("Prediction failed: %s", e)
        raise HTTPException(status_code=500, detail=f"Prediction failed: {str(e)}")


@app.get("/health", response_model=HealthResponse)
async def health_check():
    """Liveness probe."""
    return HealthResponse(
        status="ok" if pipeline is not None else "degraded",
        model_loaded=pipeline is not None,
        expected_features=len(FEATURE_COLUMNS),
        feature_columns=FEATURE_COLUMNS,
    )
