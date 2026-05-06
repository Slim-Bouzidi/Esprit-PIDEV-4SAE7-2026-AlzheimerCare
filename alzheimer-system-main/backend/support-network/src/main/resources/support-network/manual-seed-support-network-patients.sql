-- Optional manual seed for Support Network patients (AlzheimerApp).
-- Default path: Spring Boot seeds these automatically via PatientSeedRunner when
-- support_network_patient is empty (non-test profiles).
--
-- Use this file if:
--   - you already had rows and the runner skipped, or
--   - you prefer explicit DBA control.
--
-- Target DB: same MySQL as spring.datasource.url (e.g. alzheimer_db).
-- Column names follow Spring Boot default physical naming (camelCase -> snake_case).

INSERT INTO support_network_patient (id, full_name, zone, latitude, longitude) VALUES
  (101, 'Mme Fatma Ben Salah', 'Ariana', 36.8665, 10.1647),
  (102, 'M. Mohamed Sassi', 'Tunis', 36.8065, 10.1815),
  (103, 'Mme Amel Gharbi', 'Sousse', 35.8256, 10.6411)
ON DUPLICATE KEY UPDATE
  full_name = VALUES(full_name),
  zone = VALUES(zone),
  latitude = VALUES(latitude),
  longitude = VALUES(longitude);
