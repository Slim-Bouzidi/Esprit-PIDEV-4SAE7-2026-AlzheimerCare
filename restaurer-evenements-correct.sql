USE assistancequotidiennedb;

TRUNCATE TABLE evenement_agenda;

-- Patient 1: Abdenour Foudaili
INSERT INTO evenement_agenda (patient_id, type_evenement, titre, description, date_heure, statut) VALUES
(1, 'repas', 'Petit-dejeuner', 'Abdenour Foudaili', '2026-04-27 08:00:00', 'PLANIFIE'),
(1, 'medicament', 'Medicaments matin', 'Abdenour Foudaili', '2026-04-27 09:00:00', 'PLANIFIE'),
(1, 'activite', 'Toilette', 'Abdenour Foudaili', '2026-04-27 10:00:00', 'PLANIFIE'),
(1, 'repas', 'Dejeuner', 'Abdenour Foudaili', '2026-04-27 12:30:00', 'PLANIFIE'),
(1, 'medicament', 'Medicaments midi', 'Abdenour Foudaili', '2026-04-27 14:00:00', 'PLANIFIE'),
(1, 'activite', 'Activite', 'Abdenour Foudaili', '2026-04-27 15:00:00', 'PLANIFIE'),
(1, 'repas', 'Diner', 'Abdenour Foudaili', '2026-04-27 19:00:00', 'PLANIFIE'),
(1, 'medicament', 'Medicaments soir', 'Abdenour Foudaili', '2026-04-27 20:00:00', 'PLANIFIE'),
(1, 'activite', 'Coucher', 'Abdenour Foudaili', '2026-04-27 21:00:00', 'PLANIFIE');

-- Copier pour les autres jours
INSERT INTO evenement_agenda (patient_id, type_evenement, titre, description, date_heure, statut)
SELECT patient_id, type_evenement, titre, description, DATE_ADD(date_heure, INTERVAL 1 DAY), statut
FROM evenement_agenda WHERE DATE(date_heure) = '2026-04-27';

INSERT INTO evenement_agenda (patient_id, type_evenement, titre, description, date_heure, statut)
SELECT patient_id, type_evenement, titre, description, DATE_ADD(date_heure, INTERVAL 1 DAY), statut
FROM evenement_agenda WHERE DATE(date_heure) = '2026-04-28';

INSERT INTO evenement_agenda (patient_id, type_evenement, titre, description, date_heure, statut)
SELECT patient_id, type_evenement, titre, description, DATE_ADD(date_heure, INTERVAL 1 DAY), statut
FROM evenement_agenda WHERE DATE(date_heure) = '2026-04-29';

INSERT INTO evenement_agenda (patient_id, type_evenement, titre, description, date_heure, statut)
SELECT patient_id, type_evenement, titre, description, DATE_ADD(date_heure, INTERVAL 1 DAY), statut
FROM evenement_agenda WHERE DATE(date_heure) = '2026-04-30';

INSERT INTO evenement_agenda (patient_id, type_evenement, titre, description, date_heure, statut)
SELECT patient_id, type_evenement, titre, description, DATE_ADD(date_heure, INTERVAL 1 DAY), statut
FROM evenement_agenda WHERE DATE(date_heure) = '2026-05-01';

INSERT INTO evenement_agenda (patient_id, type_evenement, titre, description, date_heure, statut)
SELECT patient_id, type_evenement, titre, description, DATE_ADD(date_heure, INTERVAL 1 DAY), statut
FROM evenement_agenda WHERE DATE(date_heure) = '2026-05-02';

-- Copier pour les autres patients
INSERT INTO evenement_agenda (patient_id, type_evenement, titre, description, date_heure, statut)
SELECT 2, type_evenement, titre, 'Aziz Aziz', date_heure, statut
FROM evenement_agenda WHERE patient_id = 1;

INSERT INTO evenement_agenda (patient_id, type_evenement, titre, description, date_heure, statut)
SELECT 3, type_evenement, titre, 'Adem Adem', date_heure, statut
FROM evenement_agenda WHERE patient_id = 1;

SELECT COUNT(*) as total FROM evenement_agenda;
SELECT patient_id, COUNT(*) as nb FROM evenement_agenda GROUP BY patient_id;
