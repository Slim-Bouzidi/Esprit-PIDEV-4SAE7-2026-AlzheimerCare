-- Script SQL pour corriger les patients et tester les rapports hebdomadaires
-- Date: 2026-04-14

USE assistancequotidiennedb;

-- ÉTAPE 1: Assigner le soignant (ID 8) aux patients
UPDATE patient SET soignant_id = 8 WHERE id IN (1, 4, 5);

-- Vérifier l'assignation
SELECT id, nom_complet, soignant_id FROM patient WHERE id IN (1, 4, 5);

-- ÉTAPE 2: Insérer des fiches de test pour la semaine ACTUELLE (2026-04-14 → 2026-04-20)
-- Le scheduler est configuré pour tester la semaine actuelle

-- Fiche 1: Patient 1 (Test Patient) - Lundi 2026-04-14
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
    1,  -- Test Patient
    8,  -- Dr. Martin Soignant
    '2026-04-14',
    'envoye',
    '{"totalPris": "3", "totalPrevus": "3", "details": "Tous les médicaments pris"}',
    '{"appetit": "Bon", "quantite": "Normale"}',
    'Patient en bonne forme, bonne observance médicamenteuse',
    '2026-04-14 10:00:00',
    '2026-04-14 10:05:00'
);

-- Fiche 2: Patient 4 (Jean Dupont) - Mardi 2026-04-15
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
    8,
    '2026-04-15',
    'envoye',
    '{"totalPris": "2", "totalPrevus": "3", "details": "Un médicament oublié"}',
    '{"appetit": "Moyen", "quantite": "Réduite"}',
    'Patient un peu fatigué, a oublié un médicament',
    '2026-04-15 14:00:00',
    '2026-04-15 14:05:00'
);

-- Fiche 3: Patient 5 (abdenour) - Mercredi 2026-04-16 (date future pour test)
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
    '2026-04-16',
    'envoye',
    '{"totalPris": "3", "totalPrevus": "3", "details": "Excellente observance"}',
    '{"appetit": "Bon", "quantite": "Normale"}',
    'Patient très coopératif, excellente semaine',
    '2026-04-16 16:00:00',
    '2026-04-16 16:05:00'
);

-- ÉTAPE 3: Vérifier les fiches insérées
SELECT 
    f.id,
    p.nom_complet as patient,
    f.date_fiche,
    f.statut,
    f.commentaire_libre
FROM fiche_transmission f
JOIN patient p ON f.patient_id = p.id
WHERE f.date_fiche BETWEEN '2026-04-14' AND '2026-04-20'
AND f.statut = 'envoye'
ORDER BY f.date_fiche;

-- ÉTAPE 4: Attendre 1 minute pour que le scheduler s'exécute
-- Puis vérifier les rapports générés:

-- SELECT 
--     r.id,
--     p.nom_complet as patient,
--     r.date_debut,
--     r.date_fin,
--     r.taux_observance_medicaments,
--     r.taux_observance_repas,
--     r.envoye_au_medecin,
--     r.date_creation
-- FROM rapport_hebdomadaire r
-- JOIN patient p ON r.patient_id = p.id
-- ORDER BY r.date_creation DESC;
