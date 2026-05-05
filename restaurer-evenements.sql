-- Restaurer les evenements pour tous les patients

USE assistancequotidiennedb;

-- Supprimer les evenements existants
DELETE FROM evenement_agenda;

-- Inserer les evenements pour Patient 1 (Abdenour Foudaili)
INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, patient_nom, statut) VALUES
-- Lundi 27 avril
('Petit-dejeuner', '2026-04-27', '08:00', 'repas', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Medicaments matin', '2026-04-27', '09:00', 'medicament', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Toilette', '2026-04-27', '10:00', 'activite', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Dejeuner', '2026-04-27', '12:30', 'repas', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Medicaments midi', '2026-04-27', '14:00', 'medicament', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Activite', '2026-04-27', '15:00', 'activite', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Diner', '2026-04-27', '19:00', 'repas', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Medicaments soir', '2026-04-27', '20:00', 'medicament', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Coucher', '2026-04-27', '21:00', 'activite', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),

-- Mardi 28 avril
('Petit-dejeuner', '2026-04-28', '08:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments matin', '2026-04-28', '09:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Toilette', '2026-04-28', '10:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Dejeuner', '2026-04-28', '12:30', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments midi', '2026-04-28', '14:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Activite', '2026-04-28', '15:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Diner', '2026-04-28', '19:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments soir', '2026-04-28', '20:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Coucher', '2026-04-28', '21:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),

-- Mercredi 29 avril
('Petit-dejeuner', '2026-04-29', '08:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments matin', '2026-04-29', '09:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Toilette', '2026-04-29', '10:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Dejeuner', '2026-04-29', '12:30', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments midi', '2026-04-29', '14:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Activite', '2026-04-29', '15:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Diner', '2026-04-29', '19:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments soir', '2026-04-29', '20:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Coucher', '2026-04-29', '21:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),

-- Jeudi 30 avril
('Petit-dejeuner', '2026-04-30', '08:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments matin', '2026-04-30', '09:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Toilette', '2026-04-30', '10:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Dejeuner', '2026-04-30', '12:30', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments midi', '2026-04-30', '14:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Activite', '2026-04-30', '15:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Diner', '2026-04-30', '19:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments soir', '2026-04-30', '20:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Coucher', '2026-04-30', '21:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),

-- Vendredi 1 mai
('Petit-dejeuner', '2026-05-01', '08:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments matin', '2026-05-01', '09:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Toilette', '2026-05-01', '10:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Dejeuner', '2026-05-01', '12:30', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments midi', '2026-05-01', '14:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Activite', '2026-05-01', '15:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Diner', '2026-05-01', '19:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments soir', '2026-05-01', '20:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Coucher', '2026-05-01', '21:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),

-- Samedi 2 mai
('Petit-dejeuner', '2026-05-02', '08:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments matin', '2026-05-02', '09:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Toilette', '2026-05-02', '10:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Dejeuner', '2026-05-02', '12:30', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments midi', '2026-05-02', '14:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Activite', '2026-05-02', '15:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Diner', '2026-05-02', '19:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments soir', '2026-05-02', '20:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Coucher', '2026-05-02', '21:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),

-- Dimanche 3 mai
('Petit-dejeuner', '2026-05-03', '08:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments matin', '2026-05-03', '09:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Toilette', '2026-05-03', '10:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Dejeuner', '2026-05-03', '12:30', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments midi', '2026-05-03', '14:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Activite', '2026-05-03', '15:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente'),
('Diner', '2026-05-03', '19:00', 'repas', 'Abdenour Foudaili', 1, 'en_attente'),
('Medicaments soir', '2026-05-03', '20:00', 'medicament', 'Abdenour Foudaili', 1, 'en_attente'),
('Coucher', '2026-05-03', '21:00', 'activite', 'Abdenour Foudaili', 1, 'en_attente');

-- Inserer les evenements pour Patient 2 (Aziz Aziz) - memes evenements
INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, statut)
SELECT titre, date_evenement, heure, type, 'Aziz Aziz', 2, statut
FROM evenement_agenda WHERE patient_id = 1;

-- Inserer les evenements pour Patient 3 (Adem Adem) - memes evenements
INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, statut)
SELECT titre, date_evenement, heure, type, 'Adem Adem', 3, statut
FROM evenement_agenda WHERE patient_id = 1;

-- Verifier
SELECT COUNT(*) as total_evenements FROM evenement_agenda;
SELECT patient_id, COUNT(*) as nb_evenements FROM evenement_agenda GROUP BY patient_id;
