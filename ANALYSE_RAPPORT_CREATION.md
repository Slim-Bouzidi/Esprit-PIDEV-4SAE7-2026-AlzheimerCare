# Analyse - Création de Rapport

## Date: 14 avril 2026

## Problème
L'enregistrement des rapports via le composant `doctor-report-create` génère une erreur 500 côté backend.

## Investigation Effectuée

### 1. Structure du Code Frontend

**Composant**: `doctor-report-create.component.ts`
- Méthode `submit()` (ligne 193-405)
- Construit un objet `Rapport` avec:
  - `patient: { id: number }`
  - `soignant: { id: 1 }` (valeur en dur)
  - `typeRapport: 'HEBDOMADAIRE'`
  - `periodeDebut`, `periodeFin` (dates au format string)
  - `titre`, `contenuTexte`, `directives`, `recommandations`
  - `statut: 'GENERE'`

**Service**: `rapport.service.ts`
- Méthode `create(rapport: Rapport)` envoie directement l'objet au backend
- Endpoint: `POST /api/rapports`

### 2. Structure du Backend

**Contrôleur**: `RapportController.java`
```java
@PostMapping
public ResponseEntity<Rapport> create(@Valid @RequestBody RapportRequest request) {
    Patient patient = patientRepository.findById(request.getPatient().getId())
            .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
    
    // Validation: date fin >= date début
    if (request.getPeriodeFin().isBefore(request.getPeriodeDebut())) {
        throw new IllegalArgumentException("...");
    }
    
    // Création du rapport...
}
```

**DTO**: `RapportRequest.java`
- Validations strictes avec `@NotNull`, `@NotBlank`, `@Size`, `@Pattern`
- Champs requis:
  - `patient` (@NotNull)
  - `soignant` (@NotNull)
  - `typeRapport` (@NotBlank, @Pattern)
  - `periodeDebut` (@NotNull)
  - `periodeFin` (@NotNull)
  - `contenuTexte` (@NotBlank, @Size max 10000)

**Entité**: `Rapport.java`
- Relations:
  - `@ManyToOne Patient patient`
  - `@ManyToOne User soignant`
- Méthode `@PrePersist onCreate()` génère automatiquement:
  - `dateGeneration = LocalDateTime.now()`
  - `titre` si non fourni

### 3. Tests Effectués

#### Test 1: Création avec soignant ID 1 (PATIENT)
```json
{
  "patient": {"id": 1},
  "soignant": {"id": 1},
  "typeRapport": "HEBDOMADAIRE",
  "periodeDebut": "2026-04-07",
  "periodeFin": "2026-04-14",
  "contenuTexte": "Test"
}
```
**Résultat**: ❌ Erreur 500

#### Test 2: Création d'un user SOIGNANT
```json
{
  "nom": "Dr. Martin Soignant",
  "email": "soignant@test.com",
  "role": "SOIGNANT"
}
```
**Résultat**: ✅ User créé avec ID 8

#### Test 3: Création avec soignant ID 8 (SOIGNANT)
```json
{
  "patient": {"id": 1},
  "soignant": {"id": 8},
  "typeRapport": "HEBDOMADAIRE",
  "periodeDebut": "2026-04-07",
  "periodeFin": "2026-04-14",
  "contenuTexte": "Test"
}
```
**Résultat**: ❌ Erreur 500

#### Test 4: Rapport minimal
```json
{
  "patient": {"id": 1},
  "soignant": {"id": 8},
  "typeRapport": "HEBDOMADAIRE",
  "periodeDebut": "2026-04-07",
  "periodeFin": "2026-04-14",
  "contenuTexte": "Test contenu"
}
```
**Résultat**: ❌ Erreur 500

### 4. Problèmes Identifiés

#### A. Format des Dates
**Frontend envoie**: `periodeDebut: "2026-04-07"` (string)
**Backend attend**: `LocalDate` (sera converti automatiquement par Jackson)
**Status**: ✅ Devrait fonctionner

#### B. Soignant en Dur
**Frontend**: `soignant: { id: 1 }` (valeur codée en dur)
**Problème**: L'ID 1 était un PATIENT, pas un SOIGNANT
**Solution appliquée**: Créé un user SOIGNANT avec ID 8
**Status**: ⚠️ Toujours en erreur

#### C. Erreur 500 Persistante
**Symptôme**: Erreur 500 même avec des données valides
**Logs**: Pas d'exception claire dans les logs
**Hypothèses**:
1. Problème de contrainte de base de données
2. Problème de validation Bean Validation
3. Problème de sérialisation JSON
4. Problème de relation JPA (Patient ou User non trouvé)

### 5. Données de Test Disponibles

**Patients**:
- ID 1: Pierre Durand ✅
- ID 5-8: Autres patients ✅

**Users**:
- ID 1-7: PATIENT
- ID 8: Dr. Martin Soignant (SOIGNANT) ✅

## Recommandations

### Solution Immédiate
1. **Vérifier la table `rapport` dans la base de données**
   ```sql
   DESCRIBE rapport;
   SELECT * FROM rapport LIMIT 5;
   ```

2. **Activer les logs SQL dans Spring Boot**
   - Ajouter dans `application.properties`:
   ```properties
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   logging.level.org.hibernate.SQL=DEBUG
   logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
   ```

3. **Tester directement avec curl/Postman**
   - Éliminer les problèmes d'encodage PowerShell

4. **Vérifier les contraintes de la table**
   - Contraintes NOT NULL
   - Clés étrangères
   - Index uniques

### Solution à Long Terme

1. **Modifier le frontend pour utiliser l'ID du médecin connecté**
   ```typescript
   // Au lieu de:
   soignant: { id: 1 }
   
   // Utiliser:
   soignant: { id: this.currentUserId }
   ```

2. **Rendre le soignant optionnel dans le DTO**
   ```java
   // Changer de:
   @NotNull(message = "Le soignant est requis.")
   private SoignantRef soignant;
   
   // À:
   private SoignantRef soignant; // Optionnel
   ```

3. **Ajouter une gestion d'erreur plus détaillée**
   ```java
   @ExceptionHandler(Exception.class)
   public ResponseEntity<Map<String, String>> handleException(Exception e) {
       Map<String, String> error = new HashMap<>();
       error.put("message", e.getMessage());
       error.put("cause", e.getCause() != null ? e.getCause().getMessage() : "");
       return ResponseEntity.status(500).body(error);
   }
   ```

## État Actuel

- ✅ Patient service corrigé (ajout patient fonctionne)
- ✅ Dropdown rendez-vous corrigé (affiche les patients)
- ✅ User SOIGNANT créé (ID 8)
- ❌ Création de rapport en erreur 500
- ⏳ Cause racine non identifiée (logs insuffisants)

## Prochaines Étapes

1. Vérifier la structure de la table `rapport` dans MySQL
2. Activer les logs SQL détaillés
3. Tester avec un outil externe (Postman/curl)
4. Ajouter un gestionnaire d'exceptions global
5. Vérifier si d'autres rapports existent déjà dans la base

## Fichiers Concernés

- Frontend:
  - `doctor-report-create.component.ts` (ligne 365: soignant en dur)
  - `rapport.service.ts` (méthode create)

- Backend:
  - `RapportController.java` (méthode create)
  - `RapportRequest.java` (validations DTO)
  - `Rapport.java` (entité JPA)

## Commandes Utiles

```powershell
# Vérifier les rapports
Invoke-RestMethod -Uri "http://localhost:8090/api/rapports" -Method GET

# Voir les logs
docker logs assistance-quotidienne --tail 100

# Redémarrer le service
docker restart assistance-quotidienne

# Tester la création
$json = Get-Content rapport_minimal.json -Raw
Invoke-RestMethod -Uri "http://localhost:8098/api/rapports" -Method POST -Body $json -ContentType "application/json; charset=utf-8"
```
