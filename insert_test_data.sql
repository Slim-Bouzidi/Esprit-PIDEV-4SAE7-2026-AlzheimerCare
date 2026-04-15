-- Script SQL pour insérer des fiches de test pour la semaine dernière
-- Période: 2026-04-06 (Lundi) → 2026-04-12 (Dimanche)

USE assistancequotidiennedb;

-- Fiche 1: Patient 4 (Jean Dupont) - Lundi 2026-04-07
INSERT INTO fiche_transmission (
    patient_id, 
    soignant_id, 
    date_fiche, 
    statut, 
    observance_medicaments_json,
    alimentation_json,
    commentaire_libre,
    date_creation,
    date_envoi
) VALUES (
    4,  -- Jean Dupont
    8,  -- Dr. Martin Soignant
    '2026-04-07',
    'envoye',
    '{"totalPris": "3", "totalPrevus": "3", "details": "Tous les médicaments pris"}',
    '{"appetit": "Bon", "quantite": "Normale"}',
    'Patient en bonne forme, bonne observance médicamenteuse',
    '2026-04-07 10:00:00',
    '2026-04-07 10:05:00'
);

-- Fiche 2: Patient 4 (Jean Dupont) - Mercredi 2026-04-09
INSERT INTO fiche_transmission (
    patient_id, 
    soignant_id, 
    date_fiche, 
    statut, 
    observance_medicaments_json,
    alimentation_json,
    commentaire_libre,
    date_creation,
    date_envoi
) VALUES (
    4,
    8,
    '2026-04-09',
    'envoye',
    '{"totalPris": "2", "totalPrevus": "3", "details": "Un médicament oublié"}',
    '{"appetit": "Moyen", "quantite": "Réduite"}',
    'Patient un peu fatigué, a oublié un médicament',
    '2026-04-09 14:00:00',
    '2026-04-09 14:05:00'
);

-- Fiche 3: Patient 5 (abdenour) - Vendredi 2026-04-11
INSERT INTO fiche_transmission (
    patient_id, 
    soignant_id, 
    date_fiche, 
    statut, 
    observance_medicaments_json,
    alimentation_json,
    commentaire_libre,
    date_creation,
    date_envoi
) VALUES (
    5,  -- abdenour
    8,
    '2026-04-11',
    'envoye',
    '{"totalPris": "3", "totalPrevus": "3", "details": "Excellente observance"}',
    '{"appetit": "Bon", "quantite": "Normale"}',
    'Patient très coopératif, excellente semaine',
    '2026-04-11 16:00:00',
    '2026-04-11 16:05:00'
);

-- Vérifier les fiches insérées
SELECT 
    f.id,
    p.nom_complet as patient,
    f.date_fiche,
    f.statut,
    f.commentaire_libre
FROM fiche_transmission f
JOIN patient p ON f.patient_id = p.id
WHERE f.date_fiche BETWEEN '2026-04-06' AND '2026-04-12'
AND f.statut = 'envoye'
ORDER BY f.date_fiche;
