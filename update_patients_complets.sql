-- Mettre à jour les patients avec des noms complets corrects

UPDATE patient SET 
    nom = 'Foudaili',
    prenom = 'Abdenour',
    nom_complet = 'Abdenour Foudaili'
WHERE id = 1;

UPDATE patient SET 
    nom = 'Aziz',
    prenom = 'Aziz',
    nom_complet = 'Aziz Aziz'
WHERE id = 2;

UPDATE patient SET 
    nom = 'Adem',
    prenom = 'Adem',
    nom_complet = 'Adem Adem'
WHERE id = 3;

-- Vérifier les mises à jour
SELECT id, nom, prenom, nom_complet, actif FROM patient;
