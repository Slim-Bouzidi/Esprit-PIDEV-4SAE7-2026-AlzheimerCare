"""
═══════════════════════════════════════════════════════════════
  Alzheimer Risk — Training Script
═══════════════════════════════════════════════════════════════

  Run ONCE to generate model.pkl:
    python train_model.py

  After model.pkl is created, this script is never needed again.
"""

import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.impute import SimpleImputer
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.tree import DecisionTreeClassifier
from sklearn.metrics import accuracy_score, f1_score, classification_report
import joblib

# ── 1. Load dataset ──────────────────────────────────────────
df = pd.read_csv("alzheimers_disease_data.csv")
print(f"Dataset loaded: {df.shape[0]} rows, {df.shape[1]} columns")
print(f"Columns: {df.columns.tolist()}")

# ── 2. Define target and drop ID-like columns ────────────────
TARGET = "Diagnosis"
DROP_COLS = ["PatientID", "DoctorInCharge"]

X = df.drop(columns=[TARGET] + [c for c in DROP_COLS if c in df.columns])
y = df[TARGET]

print(f"\nTarget: {TARGET}")
print(f"Dropped: {DROP_COLS}")
print(f"Features: {X.columns.tolist()}")
print(f"Target distribution:\n{y.value_counts().to_string()}")

# ── 3. Detect column types ───────────────────────────────────
categorical_cols = X.select_dtypes(include=["object"]).columns.tolist()
numerical_cols = X.select_dtypes(exclude=["object"]).columns.tolist()

print(f"\nNumerical columns ({len(numerical_cols)}): {numerical_cols}")
print(f"Categorical columns ({len(categorical_cols)}): {categorical_cols}")

# ── 4. Build preprocessing ───────────────────────────────────
transformers = []

if numerical_cols:
    numeric_transformer = Pipeline(steps=[
        ("imputer", SimpleImputer(strategy="median")),
        ("scaler", StandardScaler()),
    ])
    transformers.append(("num", numeric_transformer, numerical_cols))

if categorical_cols:
    categorical_transformer = Pipeline(steps=[
        ("imputer", SimpleImputer(strategy="most_frequent")),
        ("onehot", OneHotEncoder(handle_unknown="ignore")),
    ])
    transformers.append(("cat", categorical_transformer, categorical_cols))

preprocessor = ColumnTransformer(transformers=transformers)

# ── 5. Build full pipeline ───────────────────────────────────
from sklearn.ensemble import RandomForestClassifier

# ── 5. Build full pipeline ───────────────────────────────────
# Random Forest is much better at picking up subtle patterns than a single tree
model = RandomForestClassifier(
    n_estimators=100,
    max_depth=12,
    class_weight='balanced',
    random_state=42
)

# Create the full pipeline
pipeline = Pipeline(steps=[
    ('preprocessor', preprocessor),
    ('classifier', model)
])

# ── 6. Train/test split ──────────────────────────────────────
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42, stratify=y
)
print(f"\nTrain: {X_train.shape[0]} | Test: {X_test.shape[0]}")

# ── 7. Train ─────────────────────────────────────────────────
pipeline.fit(X_train, y_train)

# ── 8. Evaluate ──────────────────────────────────────────────
y_pred = pipeline.predict(X_test)
print(f"\nAccuracy: {accuracy_score(y_test, y_pred):.4f}")
print(f"F1-score: {f1_score(y_test, y_pred):.4f}")
print(f"\n{classification_report(y_test, y_pred)}")

# ── 9. Generate "Legit" Visuals ─────────────────────────────
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.metrics import confusion_matrix

# 1. Confusion Matrix Plot
plt.figure(figsize=(8, 6))
cm = confusion_matrix(y_test, y_pred)
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=['Healthy', 'At Risk'], yticklabels=['Healthy', 'At Risk'])
plt.title('Alzheimer Detection: Confusion Matrix')
plt.xlabel('Predicted Label')
plt.ylabel('True Label')
plt.savefig('confusion_matrix.png')
print("[OK] confusion_matrix.png saved!")

# 2. Feature Importance Plot
plt.figure(figsize=(10, 8))
importances = pipeline.named_steps['classifier'].feature_importances_
feat_importances = pd.Series(importances, index=X.columns)
feat_importances.nlargest(10).plot(kind='barh', color='teal')
plt.title('Top 10 Clinical Risk Factors (AI Weighting)')
plt.tight_layout()
plt.savefig('feature_importance.png')
print("[OK] feature_importance.png saved!")

# ── 10. Save model ────────────────────────────────────────────
joblib.dump(pipeline, "model.pkl")
print("[OK] model.pkl saved!")

# ── 10. Print feature list for reference ─────────────────────
print(f"\nFeature columns ({len(X.columns)}):")
for i, col in enumerate(X.columns):
    print(f"  {i}: {col}")
