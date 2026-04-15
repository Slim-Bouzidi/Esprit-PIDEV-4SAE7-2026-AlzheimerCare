-- Création de la table rapport
CREATE TABLE IF NOT EXISTS rapport (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    soignant_id BIGINT,
    type_rapport VARCHAR(50),
    periode_debut DATE,
    periode_fin DATE,
    titre TEXT,
    contenu_texte TEXT NOT NULL,
    nb_alertes INT DEFAULT 0,
    nb_interventions INT DEFAULT 0,
    taux_observance DOUBLE,
    qualite_sommeil DOUBLE,
    nb_comportements_anormaux INT DEFAULT 0,
    directives TEXT,
    recommandations TEXT,
    format_export VARCHAR(20) DEFAULT 'PDF',
    chemin_fichier VARCHAR(500),
    statut VARCHAR(20) DEFAULT 'GENERE',
    date_generation DATETIME DEFAULT CURRENT_TIMESTAMP,
    lu_par_soignant BOOLEAN DEFAULT FALSE,
    date_lecture_soignant DATETIME,
    CONSTRAINT fk_rapport_patient FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE,
    CONSTRAINT fk_rapport_soignant FOREIGN KEY (soignant_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index pour améliorer les performances
CREATE INDEX idx_rapport_patient ON rapport(patient_id);
CREATE INDEX idx_rapport_soignant ON rapport(soignant_id);
CREATE INDEX idx_rapport_date ON rapport(date_generation);
CREATE INDEX idx_rapport_statut ON rapport(statut);
