USE assistancequotidiennedb;

-- Script de nettoyage des tables inutilisees
-- Date: 2026-05-02

-- Desactiver les contraintes de cles etrangeres temporairement
SET FOREIGN_KEY_CHECKS = 0;

-- Supprimer les tables inutilisees
DROP TABLE IF EXISTS emergency_contact;
DROP TABLE IF EXISTS medical_record;
DROP TABLE IF EXISTS treatment;

-- Reactiver les contraintes
SET FOREIGN_KEY_CHECKS = 1;

-- Verifier les tables restantes
SELECT 'Tables supprimees avec succes!' as message;
SHOW TABLES;
