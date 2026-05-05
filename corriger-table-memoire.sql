USE assistancequotidiennedb;

-- Sauvegarder les donnees existantes
CREATE TABLE IF NOT EXISTS memoire_assistee_backup AS SELECT * FROM memoire_assistee;

-- Supprimer l'ancienne table
DROP TABLE IF EXISTS memoire_assistee;

-- Creer la nouvelle table selon l'entite Java
CREATE TABLE memoire_assistee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL UNIQUE,
    adresse VARCHAR(500),
    conjoint VARCHAR(255),
    infos_cles LONGTEXT,
    photos_json LONGTEXT,
    FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE,
    INDEX idx_patient (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Verifier la structure
DESCRIBE memoire_assistee;

SELECT 'Table memoire_assistee recree avec succes!' as message;
