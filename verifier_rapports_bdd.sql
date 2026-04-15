-- Script pour vérifier les rapports hebdomadaires dans la base de données

USE assistancequotidiennedb;

-- 1. Voir TOUS les rapports hebdomadaires
SELECT 
    r.id,
    p.nom_complet as patient,
    r.date_debut,
    r.date_fin,
    r.taux_observance_medicaments,
    r.taux_observance_repas,
    r.taux_observance_rendez_vous,
    r.envoye_au_medecin,
    r.date_creation,
    r.date_envoi
FROM rapport_hebdomadaire r
JOIN patient p ON r.patient_id = p.id
ORDER BY r.date_creation DESC;

-- 2. Voir les détails complets d'un rapport (avec observations)
SELECT 
    r.id,
    p.nom_complet as patient,
    r.date_debut,
    r.date_fin,
    r.taux_observance_medicaments as 'Observance Médicaments (%)',
    r.taux_observance_repas as 'Observance Repas (%)',
    r.taux_observance_rendez_vous as 'Observance RDV (%)',
    r.observations_generales as 'Observations',
    r.incidents_notables as 'Incidents',
    r.envoye_au_medecin as 'Envoyé au Médecin',
    r.date_creation as 'Date de Création',
    r.date_envoi as 'Date d\'Envoi'
FROM rapport_hebdomadaire r
JOIN patient p ON r.patient_id = p.id
ORDER BY r.date_creation DESC
LIMIT 5;

-- 3. Compter les rapports par patient
SELECT 
    p.nom_complet as patient,
    COUNT(r.id) as nombre_rapports,
    MAX(r.date_creation) as dernier_rapport
FROM patient p
LEFT JOIN rapport_hebdomadaire r ON p.id = r.patient_id
GROUP BY p.id, p.nom_complet
ORDER BY nombre_rapports DESC;

-- 4. Voir les rapports de la semaine actuelle (pour le test)
SELECT 
    r.id,
    p.nom_complet as patient,
    r.date_debut,
    r.date_fin,
    r.taux_observance_medicaments,
    r.taux_observance_repas,
    r.envoye_au_medecin,
    r.date_creation
FROM rapport_hebdomadaire r
JOIN patient p ON r.patient_id = p.id
WHERE r.date_debut >= '2026-04-14'
ORDER BY r.date_creation DESC;

-- 5. Voir la structure complète de la table
DESCRIBE rapport_hebdomadaire;
