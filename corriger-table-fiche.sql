USE assistancequotidiennedb;

-- Sauvegarder les donnees existantes si necessaire
CREATE TABLE IF NOT EXISTS fiche_transmission_backup AS SELECT * FROM fiche_transmission;

-- Supprimer l'ancienne table
DROP TABLE IF EXISTS fiche_transmission;

-- Creer la nouvelle table selon l'entite Java
CREATE TABLE fiche_transmission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    soignant_id BIGINT,
    date_fiche DATE NOT NULL,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    date_envoi DATETIME,
    statut VARCHAR(50) DEFAULT 'brouillon',
    patient_info_json TEXT,
    soignant_info_json TEXT,
    observance_medicaments_json TEXT,
    alimentation_json TEXT,
    vie_sociale_json TEXT,
    suivi_directives_json TEXT,
    signature_soignant BOOLEAN DEFAULT FALSE,
    commentaire_libre TEXT,
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (soignant_id) REFERENCES user(id),
    INDEX idx_patient_date (patient_id, date_fiche)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Verifier la structure
DESCRIBE fiche_transmission;

SELECT 'Table fiche_transmission recree avec succes!' as message;
