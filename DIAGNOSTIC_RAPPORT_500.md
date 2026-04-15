# Diagnostic - Erreur 500 Création Rapport

## Date: 14 avril 2026

## Symptôme
"Erreur lors de l'enregistrement" lors de la création d'un rapport via l'interface web.

## Tests Effectués

### ✅ Vérifications Réussies
1. **User SOIGNANT** : ID 8 (Dr. Martin Soignant) existe et a le bon role
2. **Patients** : 8 patients valides disponibles dans la base
3. **Code Frontend** : Modifié pour utiliser soignant ID 8
4. **Angular** : Recompilé avec succès
5. **Services Docker** : Tous actifs et fonctionnels
6. **Format JSON** : Conforme aux attentes du backend

### ❌ Problème Persistant
- Erreur 500 (Internal Server Error) lors de POST vers `/api/rapports`
- Les logs backend ne montrent pas l'exception racine
- Le problème se produit même avec des données minimales valides

## Hypothèses

### 1. Contrainte de Base de Données ⚠️ (PLUS PROBABLE)
La table `rapport` pourrait avoir des contraintes qui empêchent l'insertion :
- Colonnes NOT NULL manquantes
- Clés étrangères invalides
- Index uniques en conflit
- Trigger qui échoue

### 2. Problème de Validation Bean
Le DTO `RapportRequest` a des validations strictes :
- `@NotNull` sur patient et soignant
- `@NotBlank` sur contenuTexte
- `@Pattern` sur typeRapport et statut

### 3. Problème d'Enum
Les enums `TypeRapport` et `StatutRapport` pourraient ne pas correspondre :
- Frontend envoie : `"HEBDOMADAIRE"`, `"GENERE"`
- Backend attend : Valeurs exactes de l'enum

### 4. Problème de Sérialisation
Jackson pourrait échouer à désérialiser les dates ou les objets imbriqués.

## Actions de Diagnostic Recommandées

### 1. Vérifier la Structure de la Table (PRIORITAIRE)

Ouvrir XAMPP → phpMyAdmin → assistancequotidiennedb

```sql
-- Voir la structure de la table
DESCRIBE rapport;

-- Vérifier les contraintes
SHOW CREATE TABLE rapport;

-- Vérifier s'il y a des rapports existants
SELECT * FROM rapport LIMIT 5;

-- Vérifier les clés étrangères
SELECT 
    CONSTRAINT_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'rapport' 
AND TABLE_SCHEMA = 'assistancequotidiennedb'
AND REFERENCED_TABLE_NAME IS NOT NULL;
```

### 2. Activer les Logs SQL Détaillés

Modifier `application.properties` du service assistance-quotidienne :

```properties
# Logs SQL
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Logs d'erreur détaillés
logging.level.org.springframework.web=DEBUG
logging.level.assistancequotidienne2=DEBUG
```

Puis redémarrer le service :
```powershell
docker-compose down
docker-compose up -d
```

### 3. Tester avec un Outil Externe

Utiliser Postman ou curl pour éliminer les problèmes liés à PowerShell/Angular :

**Postman** :
- Method: POST
- URL: `http://localhost:8090/api/rapports`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
```json
{
  "patient": {"id": 1},
  "soignant": {"id": 8},
  "typeRapport": "HEBDOMADAIRE",
  "periodeDebut": "2026-04-07",
  "periodeFin": "2026-04-14",
  "contenuTexte": "Test",
  "statut": "GENERE"
}
```

### 4. Vérifier les Enums Backend

Vérifier que les valeurs correspondent :

**TypeRapport.java** :
```java
public enum TypeRapport {
    HEBDOMADAIRE,
    MENSUEL,
    MEDICAL,
    PERSONNALISE
}
```

**StatutRapport.java** :
```java
public enum StatutRapport {
    BROUILLON,
    GENERE,
    ENVOYE,
    ARCHIVE
}
```

### 5. Ajouter un Gestionnaire d'Exceptions Global

Dans le backend, ajouter un `@ControllerAdvice` :

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        error.put("type", e.getClass().getSimpleName());
        error.put("cause", e.getCause() != null ? e.getCause().getMessage() : "");
        
        // Log complet
        e.printStackTrace();
        
        return ResponseEntity.status(500).body(error);
    }
}
```

## Solution Temporaire

Si le problème persiste, voici une solution de contournement :

### Option 1 : Créer la Table Manuellement

Si la table n'existe pas ou est mal configurée :

```sql
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
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (soignant_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### Option 2 : Rendre le Soignant Optionnel

Modifier `RapportRequest.java` :

```java
// Changer de:
@NotNull(message = "Le soignant est requis.")
private SoignantRef soignant;

// À:
private SoignantRef soignant;  // Optionnel
```

### Option 3 : Simplifier le DTO

Créer un DTO minimal pour tester :

```java
public class RapportSimpleRequest {
    private Long patientId;
    private String contenuTexte;
    // Getters/Setters
}
```

## Fichiers de Test Créés

- `test_rapport_final.json` - Données de test pour l'API

## Commandes Utiles

```powershell
# Voir les logs en temps réel
docker logs -f assistance-quotidienne

# Redémarrer le service
docker restart assistance-quotidienne

# Tester l'API
$json = Get-Content test_rapport_final.json -Raw -Encoding UTF8
Invoke-RestMethod -Uri "http://localhost:8090/api/rapports" -Method POST -Body $json -ContentType "application/json; charset=utf-8"

# Vérifier les rapports existants
Invoke-RestMethod -Uri "http://localhost:8090/api/rapports" -Method GET
```

## Prochaines Étapes

1. **PRIORITÉ 1** : Vérifier la structure de la table `rapport` dans MySQL
2. **PRIORITÉ 2** : Activer les logs SQL détaillés
3. **PRIORITÉ 3** : Tester avec Postman pour avoir un message d'erreur détaillé
4. **PRIORITÉ 4** : Ajouter un gestionnaire d'exceptions global

## Conclusion

Le problème est côté backend (erreur 500) mais les logs ne montrent pas la cause exacte. La vérification de la structure de la table `rapport` dans MySQL est la prochaine étape critique pour identifier le problème.

Sans accès direct à la base de données ou aux logs détaillés, il est difficile de diagnostiquer plus précisément. Les recommandations ci-dessus devraient permettre d'identifier la cause racine.
