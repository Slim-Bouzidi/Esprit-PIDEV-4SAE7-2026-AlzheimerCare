USE assistancequotidiennedb;

-- Supprimer les evenements existants
TRUNCATE TABLE evenement_agenda;

-- Creer une procedure pour inserer les evenements
DELIMITER $$

CREATE TEMPORARY PROCEDURE inserer_evenements()
BEGIN
    DECLARE v_patient_id INT;
    DECLARE v_patient_nom VARCHAR(255);
    DECLARE v_date DATE;
    DECLARE done INT DEFAULT FALSE;
    
    -- Cursor pour les patients
    DECLARE cur_patients CURSOR FOR SELECT id, nom_complet FROM patient WHERE actif = 1;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur_patients;
    
    patient_loop: LOOP
        FETCH cur_patients INTO v_patient_id, v_patient_nom;
        IF done THEN
            LEAVE patient_loop;
        END IF;
        
        -- Inserer pour chaque jour de la semaine
        SET v_date = '2026-04-27';
        WHILE v_date <= '2026-05-03' DO
            INSERT INTO evenement_agenda (titre, date_evenement, heure, type, detail, patient_id, patient_nom, statut) VALUES
            ('Petit-dejeuner', v_date, '08:00', 'repas', v_patient_nom, v_patient_id, v_patient_nom, 'en_attente'),
            ('Medicaments matin', v_date, '09:00', 'medicament', v_patient_nom, v_patient_id, v_patient_nom, 'en_attente'),
            ('Toilette', v_date, '10:00', 'activite', v_patient_nom, v_patient_id, v_patient_nom, 'en_attente'),
            ('Dejeuner', v_date, '12:30', 'repas', v_patient_nom, v_patient_id, v_patient_nom, 'en_attente'),
            ('Medicaments midi', v_date, '14:00', 'medicament', v_patient_nom, v_patient_id, v_patient_nom, 'en_attente'),
            ('Activite', v_date, '15:00', 'activite', v_patient_nom, v_patient_id, v_patient_nom, 'en_attente'),
            ('Diner', v_date, '19:00', 'repas', v_patient_nom, v_patient_id, v_patient_nom, 'en_attente'),
            ('Medicaments soir', v_date, '20:00', 'medicament', v_patient_nom, v_patient_id, v_patient_nom, 'en_attente'),
            ('Coucher', v_date, '21:00', 'activite', v_patient_nom, v_patient_id, v_patient_nom, 'en_attente');
            
            SET v_date = DATE_ADD(v_date, INTERVAL 1 DAY);
        END WHILE;
    END LOOP;
    
    CLOSE cur_patients;
END$$

DELIMITER ;

-- Executer la procedure
CALL inserer_evenements();

-- Verifier
SELECT COUNT(*) as total_evenements FROM evenement_agenda;
SELECT patient_id, patient_nom, COUNT(*) as nb_evenements FROM evenement_agenda GROUP BY patient_id, patient_nom;
