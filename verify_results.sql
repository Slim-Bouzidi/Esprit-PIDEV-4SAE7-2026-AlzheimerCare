-- Script SQL pour vérifier les résultats du test

USE assistancequotidiennedb;

-- 1. Vérifier les fiches de la semaine dernière
SELECT '=== FICHES DE LA SEMAINE DERNIÈRE ===' as '';
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

-- 2. Vérifier les rapports hebdomadaires générés
SELECT '=== RAPPORTS HEBDOMADAIRES GÉNÉRÉS ===' as '';
SELECT 
    r.id,
    p.nom_complet as patient,
    r.date_debut,
    r.date_fin,
    r.taux_observance_medicaments,
    r.taux_observance_repas,
    r.taux_observance_rendez_vous,
    r.envoye_au_medecin,
    r.date_creation
FROM rapport_hebdomadaire r
JOIN patient p ON r.patient_id = p.id
WHERE r.date_debut = '2026-04-06' AND r.date_fin = '2026-04-12'
ORDER BY r.date_creation DESC;

-- 3. Vérifier les observations d'un rapport (remplacer [ID] par l'ID réel)
SELECT '=== DÉTAILS DU RAPPORT (Patient 4 - Jean Dupont) ===' as '';
SELECT 
    r.id,
    p.nom_complet as patient,
    r.observations_generales,
    r.taux_observance_medicaments,
    r.taux_observance_repas
FROM rapport_hebdomadaire r
JOIN patient p ON r.patient_id = p.id
WHERE r.patient_id = 4 
AND r.date_debut = '2026-04-06' 
AND r.date_fin = '2026-04-12'
LIMIT 1;

-- 4. Vérifier les notifications créées
SELECT '=== NOTIFICATIONS CRÉÉES ===' as '';
SELECT 
    n.id,
    n.type,
    n.titre,
    n.message,
    n.reference_id,
    n.date_creation
FROM notification n
WHERE n.type = 'RAPPORT_HEBDOMADAIRE'
AND n.date_creation >= '2026-04-14'
ORDER BY n.date_creation DESC;

-- 5. Vérifier qu'il n'y a pas de doublons
SELECT '=== VÉRIFICATION DES DOUBLONS ===' as '';
SELECT 
    patient_id,
    date_debut,
    date_fin,
    COUNT(*) as nombre_rapports
FROM rapport_hebdomadaire
WHERE date_debut = '2026-04-06' AND date_fin = '2026-04-12'
GROUP BY patient_id, date_debut, date_fin
HAVING COUNT(*) > 1;

-- Si aucune ligne n'est retournée, c'est bon (pas de doublons)
