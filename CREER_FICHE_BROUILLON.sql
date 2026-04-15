-- Script SQL pour créer une fiche de transmission en BROUILLON
-- À exécuter dans phpMyAdmin (XAMPP)

-- 1. Sélectionne la base de données
USE assistancequotidiennedb;

-- 2. Crée une fiche avec statut BROUILLON (non envoyée)
INSERT INTO fiche_transmission 
(
    patient_id, 
    soignant_id, 
    date_fiche, 
    date_creation, 
    date_envoi,
    statut, 
    signature_soignant, 
    commentaire_libre
) 
VALUES 
(
    1,                              -- patient_id (Patient avec ID 1)
    8,                              -- soignant_id (Soignant avec ID 8)
    '2026-04-14',                   -- date_fiche
    NOW(),                          -- date_creation
    NULL,                           -- date_envoi = NULL (pas encore envoyée!)
    'brouillon',                    -- statut = brouillon (PAS "envoye")
    1,                              -- signature_soignant = true (signée)
    'TEST BOUTON ENVOYER - Cliquer sur le bouton pour envoyer'  -- commentaire
);

-- 3. Vérifie que la fiche a été créée
SELECT 
    id, 
    patient_id, 
    date_fiche, 
    statut, 
    date_envoi, 
    signature_soignant,
    commentaire_libre
FROM fiche_transmission 
WHERE statut = 'brouillon'
ORDER BY id DESC 
LIMIT 1;

-- Résultat attendu:
-- - statut = "brouillon"
-- - date_envoi = NULL
-- - signature_soignant = 1
