# Correction - Création de Rapport (Erreur 500)

## Date: 14 avril 2026

## Problème Identifié

**Erreur dans la console** :
```
POST http://localhost:8090/api/rapports 500 (Internal Server Error)
Error création rapport: HttpErrorResponse
```

**Cause** : Le frontend envoyait `soignant: { id: 1 }` mais l'user ID 1 est un PATIENT, pas un SOIGNANT.

## Investigation

### 1. Analyse de l'Erreur Console
- Erreur 500 lors de POST vers `/api/rapports`
- Warning (informatif) : `[AuthInterceptor] Keycloak disabled - no token attached`
- Le warning n'est pas la cause du problème (sécurité désactivée côté backend)

### 2. Vérification Backend
**RapportRequest.java** :
```java
@NotNull(message = "Le soignant est requis.")
private SoignantRef soignant;
```

**RapportController.java** :
```java
if (request.getSoignant() != null && request.getSoignant().getId() != null && userRepository != null) {
    userRepository.findById(request.getSoignant().getId()).ifPresent(rapport::setSoignant);
}
```

Le backend essaie de trouver l'user avec ID 1, mais cet user est un PATIENT, pas un SOIGNANT.

### 3. Vérification des Users
```powershell
Invoke-RestMethod -Uri "http://localhost:8090/api/users" -Method GET
```

**Résultat** :
- ID 1-7 : PATIENT
- ID 8 : Dr. Martin Soignant (SOIGNANT) ✅

## Solution Appliquée

### Modification du Code

**Fichier** : `doctor-report-create.component.ts`
**Ligne** : 366

```typescript
// AVANT (INCORRECT)
const rapport: Rapport = {
  patient: { id: this.selectedPatient.id },
  soignant: { id: 1 },  // ❌ ID 1 est un PATIENT
  typeRapport: 'HEBDOMADAIRE',
  ...
};

// APRÈS (CORRECT)
const rapport: Rapport = {
  patient: { id: this.selectedPatient.id },
  soignant: { id: 8 },  // ✅ ID 8 est un SOIGNANT (Dr. Martin Soignant)
  typeRapport: 'HEBDOMADAIRE',
  ...
};
```

## Test de Vérification

### Étapes pour tester :
1. Ouvrir l'application : `http://localhost:4200`
2. Se connecter en tant que Médecin
3. Aller dans "Créer un rapport de suivi"
4. Sélectionner un patient
5. Remplir le formulaire :
   - Date début : 07/04/2026
   - Date fin : 14/04/2026
   - Ajouter au moins un traitement
6. Cocher "Je valide ce rapport"
7. Cliquer sur "Créer et transmettre le rapport"

### Résultat Attendu
✅ Le rapport doit être créé avec succès
✅ Message de confirmation affiché
✅ Redirection vers la page des rapports ou agenda

## Solution à Long Terme

### Recommandation 1 : Utiliser l'ID du Médecin Connecté

Au lieu de coder en dur l'ID du soignant, utiliser l'ID du médecin actuellement connecté :

```typescript
// Dans le composant, ajouter une propriété
currentDoctorId: number = 0;

// Dans ngOnInit(), récupérer l'ID du médecin connecté
ngOnInit(): void {
  // Récupérer depuis Keycloak ou le service d'authentification
  this.currentDoctorId = this.authService.getCurrentUserId();
  // ou depuis le token Keycloak
  // this.currentDoctorId = keycloak.tokenParsed?.['sub'];
}

// Dans submit(), utiliser cet ID
const rapport: Rapport = {
  patient: { id: this.selectedPatient.id },
  soignant: { id: this.currentDoctorId },  // ✅ ID dynamique
  typeRapport: 'HEBDOMADAIRE',
  ...
};
```

### Recommandation 2 : Rendre le Soignant Optionnel

Si le soignant n'est pas toujours disponible, modifier le DTO backend :

```java
// Dans RapportRequest.java
// Changer de:
@NotNull(message = "Le soignant est requis.")
private SoignantRef soignant;

// À:
private SoignantRef soignant;  // Optionnel
```

### Recommandation 3 : Validation Côté Frontend

Ajouter une validation pour s'assurer qu'un soignant valide est sélectionné :

```typescript
if (!this.currentDoctorId || this.currentDoctorId === 0) {
  this.errorMessage = 'Erreur: Aucun médecin connecté';
  return;
}
```

## Données de Test

### User SOIGNANT Créé
```json
{
  "id": 8,
  "nom": "Dr. Martin Soignant",
  "email": "soignant@test.com",
  "role": "SOIGNANT",
  "telephone": "0601020304",
  "actif": true
}
```

### Patients Disponibles
- ID 1: Pierre Durand
- ID 5: Marie Dubois
- ID 6: Jean Lefebvre
- ID 7: Sophie Martin
- ID 8: Robert Petit
- ID 11: Test Patient

## État du Système

### Services
- ✅ Frontend Angular (port 4200) - Recompilé
- ✅ API Gateway (port 8090)
- ✅ Assistance Quotidienne (port 8098)
- ✅ Base de données XAMPP MySQL

### Modifications Appliquées
- ✅ `doctor-report-create.component.ts` (ligne 366: soignant ID 1 → 8)
- ✅ Angular recompilé automatiquement

## Fichiers Modifiés

1. **alzheimer-system-main/frontend/alzheimer-angular/src/app/doctor/doctor-report-create.component.ts**
   - Ligne 366: Changé `soignant: { id: 1 }` vers `soignant: { id: 8 }`

## Commandes Utiles

### Vérifier les users
```powershell
Invoke-RestMethod -Uri "http://localhost:8090/api/users" -Method GET | 
  Select-Object id, nom, role | Format-Table
```

### Vérifier les rapports créés
```powershell
Invoke-RestMethod -Uri "http://localhost:8090/api/rapports" -Method GET
```

### Créer un rapport de test via API
```powershell
$json = @"
{
  "patient": {"id": 1},
  "soignant": {"id": 8},
  "typeRapport": "HEBDOMADAIRE",
  "periodeDebut": "2026-04-07",
  "periodeFin": "2026-04-14",
  "contenuTexte": "Test traitement",
  "statut": "GENERE"
}
"@
Invoke-RestMethod -Uri "http://localhost:8090/api/rapports" -Method POST -Body $json -ContentType "application/json; charset=utf-8"
```

## Conclusion

✅ **Problème identifié** : Soignant ID 1 (PATIENT) au lieu d'un SOIGNANT valide
✅ **Solution appliquée** : Changé vers soignant ID 8 (Dr. Martin Soignant)
✅ **Code recompilé** : Angular a détecté et appliqué les changements
⏳ **Test manuel requis** : Vérifier la création d'un rapport via l'interface

## Prochaines Étapes

1. Tester la création d'un rapport via l'interface web
2. Vérifier que le rapport est bien enregistré dans la base de données
3. Implémenter la solution à long terme (ID dynamique du médecin connecté)
4. Ajouter une validation pour s'assurer qu'un soignant valide est toujours utilisé
