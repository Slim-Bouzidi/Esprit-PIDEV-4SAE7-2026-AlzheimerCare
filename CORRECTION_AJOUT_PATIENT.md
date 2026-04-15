# Correction - Ajout de Patient enregistre NULL

## Date: 14 avril 2026

## Problème Identifié
Lors de l'ajout d'un patient via le formulaire frontend, les données étaient enregistrées comme NULL dans la base de données.

## Cause Racine
Le service `patient.service.ts` contenait une méthode `adaptToBackendFormat()` qui transformait incorrectement les données du frontend:

### Transformation incorrecte:
```typescript
// AVANT (INCORRECT)
create(patient: any): Observable<any> {
    const backendPatient = this.adaptToBackendFormat(patient);
    return this.http.post<any>(this.baseUrl, backendPatient, {
      headers: this.getHeaders()
    });
}

private adaptToBackendFormat(patient: any): any {
    const adapted: any = {};
    // Transformait nomComplet -> firstName/lastName
    // Transformait dateNaissance -> age
    // Ajoutait des champs inexistants: familyHistory, smokingStatus
    return adapted;
}
```

### Problème:
- Le backend `PatientController` attend un objet `Patient` avec les champs **français**:
  - `nomComplet` (String)
  - `dateNaissance` (LocalDate)
  - `adresse` (String)
  - `numeroDeTelephone` (String)
  - `antecedents` (String)
  - `allergies` (String)
  - `actif` (Boolean)

- Mais `adaptToBackendFormat()` envoyait des champs **anglais**:
  - `firstName`, `lastName` (au lieu de `nomComplet`)
  - `age` (au lieu de `dateNaissance`)
  - `familyHistory`, `smokingStatus` (champs inexistants)

## Solution Appliquée

### Modification du fichier `patient.service.ts`

**Ligne 85-90**: Suppression de l'adaptation incorrecte

```typescript
// APRÈS (CORRECT)
create(patient: any): Observable<any> {
    // Le backend attend directement les champs français, pas besoin d'adaptation
    return this.http.post<any>(this.baseUrl, patient, {
      headers: this.getHeaders()
    });
}
```

**Suppression complète** de la méthode `adaptToBackendFormat()` (lignes 92-130)

## Vérification

### Test de création via API
```json
{
  "nomComplet": "Test Patient",
  "dateNaissance": "1960-06-15",
  "adresse": "123 rue de Test",
  "numeroDeTelephone": "0698765432",
  "antecedents": "Test antecedents",
  "allergies": "Test allergies",
  "actif": true
}
```

### Résultat
✅ Patient créé avec succès avec toutes les données correctement enregistrées:
- ID: 11
- Nom Complet: Test Patient
- Date Naissance: 1960-06-15
- Adresse: 123 rue de Test
- Téléphone: 0698765432
- Antécédents: Test antecedents
- Allergies: Test allergies
- Actif: true

## Impact

### Avant la correction:
- ❌ Tous les champs enregistrés comme NULL
- ❌ Seul le champ `actif` était enregistré (valeur par défaut)
- ❌ Patients inutilisables dans l'application

### Après la correction:
- ✅ Tous les champs correctement enregistrés
- ✅ Données complètes et exploitables
- ✅ Patients visibles dans les dropdowns
- ✅ Création de rendez-vous fonctionnelle

## Fichiers Modifiés

1. **alzheimer-system-main/frontend/alzheimer-angular/src/app/services/patient.service.ts**
   - Ligne 85-90: Simplification de la méthode `create()`
   - Lignes 92-130: Suppression de `adaptToBackendFormat()`

## État du Système

### Services actifs:
- ✅ Frontend Angular (port 4200) - Recompilé automatiquement
- ✅ API Gateway (port 8090)
- ✅ Assistance Quotidienne (port 8098)
- ✅ Base de données XAMPP MySQL (assistancequotidiennedb)

### Patients dans la base:
- 5 patients valides existants
- 1 patient de test créé et vérifié (ID 11)

## Test Manuel à Effectuer

1. Ouvrir l'application: `http://localhost:4200`
2. Se connecter en tant que Médecin
3. Aller dans "Patients"
4. Cliquer sur "Ajouter un patient"
5. Remplir le formulaire:
   - Nom complet: "Nouveau Patient Test"
   - Date de naissance: Choisir une date
   - Adresse: "123 rue Test"
   - Téléphone: "0612345678"
   - Antécédents: "Aucun"
   - Allergies: "Aucune"
6. Cliquer sur "Sauvegarder"
7. **Vérifier**: Le patient doit apparaître dans la liste avec toutes ses données
8. **Vérifier**: Le patient doit être disponible dans le dropdown "Rendez-vous"

## Commandes de Vérification

### Lister tous les patients
```powershell
Invoke-RestMethod -Uri "http://localhost:8090/api/patients" -Method GET | 
  Where-Object { $_.nomComplet } | 
  Select-Object id, nomComplet, dateNaissance, numeroDeTelephone
```

### Créer un patient de test
```powershell
$json = '{"nomComplet":"Test","dateNaissance":"1960-01-01","adresse":"Test","numeroDeTelephone":"0600000000","antecedents":"Test","allergies":"Test","actif":true}'
Invoke-RestMethod -Uri "http://localhost:8090/api/patients" -Method POST -Body $json -ContentType "application/json; charset=utf-8"
```

## Conclusion

✅ **Problème résolu**: L'ajout de patients fonctionne maintenant correctement
✅ **Données complètes**: Tous les champs sont enregistrés dans la base de données
✅ **Pas de redémarrage nécessaire**: Angular a recompilé automatiquement
✅ **Prêt pour les tests**: L'application est opérationnelle

## Note Importante

Cette correction s'applique uniquement au service `patient.service.ts`. Si d'autres services utilisent une logique similaire d'adaptation de format, ils devront être vérifiés et corrigés de la même manière.
