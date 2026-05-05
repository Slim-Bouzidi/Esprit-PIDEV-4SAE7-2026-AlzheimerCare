USE assistancequotidiennedb;

-- Script de nettoyage complet des tables inutilisees
-- Date: 2026-05-02

SET FOREIGN_KEY_CHECKS = 0;

-- Supprimer les tables de backup (inutiles)
DROP TABLE IF EXISTS memoire_assistee_backup;
DROP TABLE IF EXISTS fiche_transmission_backup;

-- Supprimer les tables vides et inutilisees
-- ATTENTION: emergency_contact, medical_record, treatment ont ete recrees par JPA
-- Il faut les supprimer a nouveau
DROP TABLE IF EXISTS emergency_contact;
DROP TABLE IF EXISTS medical_record;
DROP TABLE IF EXISTS treatment;

-- Supprimer rendez_vous (vide et non utilisee)
DROP TABLE IF EXISTS rendez_vous;

-- NE PAS supprimer 'user' et 'notification' car elles sont utilisees par d'autres tables

SET FOREIGN_KEY_CHECKS = 1;

-- Verifier les tables restantes
SELECT 'Nettoyage termine!' as message;
SHOW TABLES;
