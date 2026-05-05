USE assistancequotidiennedb;

TRUNCATE TABLE evenement_agenda;

-- Patient 1: Abdenour Foudaili - Semaine complete
INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, patient_nom, statut) VALUES
('Petit-dejeuner', '2026-04-27', '08:00', 'repas', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Medicaments matin', '2026-04-27', '09:00', 'medicament', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Toilette', '2026-04-27', '10:00', 'activite', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Dejeuner', '2026-04-27', '12:30', 'repas', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Medicaments midi', '2026-04-27', '14:00', 'medicament', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Activite', '2026-04-27', '15:00', 'activite', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Diner', '2026-04-27', '19:00', 'repas', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Medicaments soir', '2026-04-27', '20:00', 'medicament', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente'),
('Coucher', '2026-04-27', '21:00', 'activite', 'Abdenour Foudaili', 1, 'Abdenour Foudaili', 'en_attente');

-- Copier pour les autres jours et patients
INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, patient_nom, statut)
SELECT titre, DATE_ADD(date_evenement, INTERVAL 1 DAY), heure, type, detail, patient_id, patient_nom, statut
FROM evenement_agenda WHERE date_evenement = '2026-04-27';

INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, patient_nom, statut)
SELECT titre, DATE_ADD(date_evenement, INTERVAL 1 DAY), heure, type, detail, patient_id, patient_nom, statut
FROM evenement_agenda WHERE date_evenement = '2026-04-28';

INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, patient_nom, statut)
SELECT titre, DATE_ADD(date_evenement, INTERVAL 1 DAY), heure, type, detail, patient_id, patient_nom, statut
FROM evenement_agenda WHERE date_evenement = '2026-04-29';

INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, patient_nom, statut)
SELECT titre, DATE_ADD(date_evenement, INTERVAL 1 DAY), heure, type, detail, patient_id, patient_nom, statut
FROM evenement_agenda WHERE date_evenement = '2026-04-30';

INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, patient_nom, statut)
SELECT titre, DATE_ADD(date_evenement, INTERVAL 1 DAY), heure, type, detail, patient_id, patient_nom, statut
FROM evenement_agenda WHERE date_evenement = '2026-05-01';

INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, patient_nom, statut)
SELECT titre, DATE_ADD(date_evenement, INTERVAL 1 DAY), heure, type, detail, patient_id, patient_nom, statut
FROM evenement_agenda WHERE date_evenement = '2026-05-02';

-- Maintenant copier pour les autres patients
INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, patient_nom, statut)
SELECT titre, date_evenement, heure, type, 'Aziz Aziz', 2, 'Aziz Aziz', statut
FROM evenement_agenda WHERE patient_id = 1;

INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, patient_nom, statut)
SELECT titre, date_evenement, heure, type, 'Adem Adem', 3, 'Adem Adem', statut
FROM evenement_agenda WHERE patient_id = 1;

SELECT COUNT(*) as total FROM evenement_agenda;
SELECT patient_nom, COUNT(*) as nb FROM evenement_agenda GROUP BY patient_nom;
