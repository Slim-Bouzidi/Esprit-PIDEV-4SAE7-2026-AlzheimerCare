# Solution Finale - Création Table Rapport

## Problème Identifié
La table `rapport` n'existe pas dans la base de données `assistancequotidiennedb`.

## Solution : Créer la Table Manuellement

### Étape 1 : Ouvrir phpMyAdmin
1. Ouvrez XAMPP Control Panel
2. Cliquez sur "Admin" à côté de MySQL
3. Sélectionnez la base de données `assistancequotidiennedb`

### Étape 2 : Exécuter le Script SQL
1. Cliquez sur l'onglet "SQL"
2. Copiez et collez le script ci-dessous
3. Cliquez sur "Exécuter"

```sql
-- Création de la table rapport
CREATE TABLE IF NOT EXISTS rapport (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    soignant_id BIGINT,
    type_rapport VARCHAR(50),
    periode_debut DATE,
    periode_fin DATE,
    titre TEXT,
    contenu_texte TEXT NOT NULL,
    nb_alertes INT DEFAULT 0,
    nb_interventions INT DEFAULT 0,
    taux_observance DOUBLE,
    qualite_sommeil DOUBLE,
    nb_comportements_anormaux INT DEFAULT 0,
    directives TEXT,
    recommandations TEXT,
    format_export VARCHAR(20) DEFAULT 'PDF',
    chemin_fichier VARCHAR(500),
    statut VARCHAR(20) DEFAULT 'GENERE',
    date_generation DATETIME DEFAULT CURRENT_TIMESTAMP,
    lu_par_soignant BOOLEAN DEFAULT FALSE,
    date_lecture_soignant DATETIME,
    CONSTRAINT fk_rapport_patient FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE,
    CONSTRAINT fk_rapport_soignant FOREIGN KEY (soignant_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index pour améliorer les performances
CREATE INDEX idx_rapport_patient ON rapport(patient_id);
CREATE INDEX idx_rapport_soignant ON rapport(soignant_id);
CREATE INDEX idx_rapport_date ON rapport(date_generation);
CREATE INDEX idx_rapport_statut ON rapport(statut);
```

### Étape 3 : Vérifier la Création
Exécutez cette commande pour vérifier :
```sql
DESCRIBE rapport;
```

Vous devriez voir toutes les colonnes de la table.

### Étape 4 : Tester la Création d'un Rapport

#### Via l'Interface Web
1. Ouvrez http://localhost:4200
2. Connectez-vous en tant que Médecin
3. Allez dans "Créer un rapport de suivi"
4. Sélectionnez un patient (ex: Pierre Durand)
5. Remplissez le formulaire :
   - Date début : 07/04/2026
   - Date fin : 14/04/2026
   - Ajoutez au moins un traitement
6. Cochez "Je valide ce rapport"
7. Cliquez sur "Créer et transmettre le rapport"

#### Via PowerShell (Test API)
```powershell
$json = @"
{
  "patient": {"id": 1},
  "soignant": {"id": 8},
  "typeRapport": "HEBDOMADAIRE",
  "periodeDebut": "2026-04-07",
  "periodeFin": "2026-04-14",
  "titre": "Rapport de suivi - Pierre Durand",
  "contenuTexte": "Medicament A (10mg) - matin : Suivi regulier",
  "directives": "Alimentation: Regime equilibre",
  "recommandations": "Suivi regulier recommande",
  "statut": "GENERE"
}
"@

Invoke-RestMethod -Uri "http://localhost:8090/api/rapports" -Method POST -Body $json -ContentType "application/json; charset=utf-8"
```

### Étape 5 : Vérifier le Rapport Créé

#### Dans phpMyAdmin
```sql
SELECT * FROM rapport;
```

#### Via l'API
```powershell
Invoke-RestMethod -Uri "http://localhost:8090/api/rapports" -Method GET
```

## Pourquoi la Table N'a Pas Été Créée Automatiquement ?

Bien que `spring.jpa.hibernate.ddl-auto=update` soit configuré, plusieurs raisons peuvent expliquer pourquoi la table n'a pas été créée :

1. **Problème de connexion** : Le service Docker ne pouvait pas se connecter à XAMPP MySQL
2. **Timing** : La base de données n'était pas prête au démarrage du service
3. **Erreur silencieuse** : Hibernate a échoué sans logger l'erreur
4. **Configuration** : La stratégie de nommage ou le dialecte pourrait causer des problèmes

## Configuration Backend Appliquée

Le fichier `application.properties` a été modifié pour utiliser `host.docker.internal` :

```properties
spring.datasource.url=jdbc:mysql://host.docker.internal:3306/assistancequotidiennedb?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.jpa.hibernate.ddl-auto=update
```

## Fichiers Créés

- `create_table_rapport.sql` - Script SQL pour créer la table
- `test_rapport_apres_creation.json` - Données de test

## Résumé des Corrections Appliquées

### Session Complète
1. ✅ Dropdown Rendez-vous : Patients affichés correctement
2. ✅ Ajout Patient : Données enregistrées (plus de NULL)
3. ✅ Création Rapport : Soignant ID corrigé (1 → 8)
4. ⏳ Table Rapport : À créer manuellement dans phpMyAdmin

## Après Création de la Table

Une fois la table créée, tous les problèmes devraient être résolus :
- ✅ Création de rapports fonctionnelle
- ✅ Affichage des rapports dans l'interface
- ✅ Modification et suppression de rapports
- ✅ Toutes les fonctionnalités de l'application opérationnelles

## Support

Si le problème persiste après création de la table :
1. Vérifiez les logs : `docker logs assistance-quotidienne --tail 50`
2. Vérifiez la connexion MySQL : `docker exec assistance-quotidienne ping host.docker.internal`
3. Redémarrez le service : `docker restart assistance-quotidienne`

## Commandes Utiles

```powershell
# Vérifier les tables existantes
# Dans phpMyAdmin : SHOW TABLES;

# Voir la structure de la table
# Dans phpMyAdmin : DESCRIBE rapport;

# Compter les rapports
# Dans phpMyAdmin : SELECT COUNT(*) FROM rapport;

# Redémarrer le service
docker restart assistance-quotidienne

# Voir les logs
docker logs assistance-quotidienne --tail 100
```
