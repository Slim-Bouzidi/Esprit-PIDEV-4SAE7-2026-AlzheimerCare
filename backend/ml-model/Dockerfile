# ═══════════════════════════════════════════════════════════════
#  Alzheimer ML Prediction Service — Dockerfile
# ═══════════════════════════════════════════════════════════════
FROM python:3.10-slim

LABEL maintainer="alzheimer-system"
LABEL description="FastAPI ML microservice for Alzheimer risk prediction"

# Prevent Python from buffering stdout/stderr (important for Docker logs)
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

# ── Install dependencies first (cache-friendly) ──────────────
COPY requirements.txt .
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# ── Copy application code and model ──────────────────────────
COPY model.py .
COPY app.py .
COPY model.pkl .

EXPOSE 5000

# ── Run ───────────────────────────────────────────────────────
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "5000"]
