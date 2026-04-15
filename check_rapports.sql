-- Vérifier les rapports hebdomadaires existants
USE assistancequotidiennedb;

-- Voir tous les rapports
SELECT 
    r.id,
    p.nom_complet as patient,
    r.date_debut,
    r.date_fin,
    r.taux_observance_medicaments,
    r.taux_observance_repas,
    r.envoye_au_medecin,
    r.soignant_id,
    r.date_creation
FROM rapport_hebdomadaire r
LEFT JOIN patient p ON r.patient_id = p.id
ORDER BY r.date_creation DESC;

-- Compter les rapports
SELECT COUNT(*) as total_rapports FROM rapport_hebdomadaire;

-- Vérifier les patients et leurs soignants
SELECT 
    id, 
    nom_complet, 
    soignant_id,
    CASE 
        WHEN soignant_id IS NULL THEN '❌ Pas de soignant'
        ELSE '✅ Soignant assigné'
    END as statut
FROM patient 
WHERE id IN (1, 4, 5);

-- Vérifier les fiches avec statut "envoye"
SELECT 
    f.id,
    p.nom_complet as patient,
    f.date_fiche,
    f.statut,
    f.date_creation
FROM fiche_transmission f
JOIN patient p ON f.patient_id = p.id
WHERE f.statut = 'envoye'
ORDER BY f.date_creation DESC
LIMIT 10;
