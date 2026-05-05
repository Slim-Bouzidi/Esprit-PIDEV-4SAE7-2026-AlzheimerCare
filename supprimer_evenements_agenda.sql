-- Supprimer tous les événements de l'agenda
DELETE FROM evenement_agenda;

-- Vérifier que la table est vide
SELECT COUNT(*) as nombre_evenements FROM evenement_agenda;

-- Afficher un message
SELECT 'Tous les evenements ont ete supprimes' as message;
