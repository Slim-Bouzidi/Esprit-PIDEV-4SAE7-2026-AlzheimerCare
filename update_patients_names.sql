-- Mettre à jour les noms des patients existants

UPDATE patient SET 
    nom = 'Aziz',
    prenom = 'Aziz',
    nom_complet = 'Aziz Aziz'
WHERE id = 1;

UPDATE patient SET 
    nom = 'Foudalii',
    prenom = 'Abdenour',
    nom_complet = 'Abdenour Foudalii'
WHERE id = 2;

UPDATE patient SET 
    nom = 'Adem',
    prenom = 'Adem',
    nom_complet = 'Adem Adem'
WHERE id = 3;

-- Vérifier les mises à jour
SELECT id, nom, prenom, nom_complet, actif FROM patient;
