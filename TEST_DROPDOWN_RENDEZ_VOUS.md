# Test du Dropdown Patients dans Rendez-vous

## Date du test
14 avril 2026

## Objectif
Vérifier que le dropdown "Patient" dans le formulaire "Planifier un Rendez-vous" affiche uniquement les patients de la table `patient` de la base `assistancequotidiennedb`.

## Configuration
- Base de données: `assistancequotidiennedb` (XAMPP MySQL sur localhost:3306)
- Backend: Assistance Quotidienne (port 8098)
- API Gateway: port 8090
- Frontend Angular: port 4200

## Modifications effectuées

### 1. Modification du composant TypeScript
**Fichier**: `alzheimer-system-main/frontend/alzheimer-angular/src/app/doctor/doctor-appointments.component.ts`

**Changement**:
```typescript
// AVANT: Chargeait depuis /api/users (table user avec role PATIENT)
this.patientService.getAllFromUsers().subscribe(...)

// APRÈS: Charge depuis /api/patients (table patient)
this.patientService.getAll().subscribe({
    next: (patients) => {
        // Filtrer uniquement les patients avec nomComplet (données valides)
        this.patients = patients.filter(p => p.nomComplet && p.nomComplet.trim() !== '');
        console.log('Patients chargés depuis la table patient:', this.patients);
    },
    error: (err) => {
        console.error('Erreur chargement patients:', err);
        this.loadMockPatients();
    }
});
```

### 2. HTML déjà correct
Le template HTML utilise déjà `[ngValue]="patient"` (l'objet complet) au lieu de `[ngValue]="patient.id"`, ce qui est correct pour Angular.

## Patients dans la base de données

| ID | Nom Complet    | Date Naissance | Téléphone   |
|----|----------------|----------------|-------------|
| 1  | Pierre Durand  | 1952-11-08     | 0667890123  |
| 5  | Marie Dubois   | 1948-05-15     | 0612345678  |
| 6  | Jean Lefebvre  | 1945-08-22     | 0623456789  |
| 7  | Sophie Martin  | 1950-03-10     | 0634567890  |
| 8  | Robert Petit   | 1942-12-05     | 0645678901  |

**Total**: 5 patients valides

## Étapes de test

### 1. Accéder à l'application
```
URL: http://localhost:4200
```

### 2. Se connecter en tant que Médecin
- Naviguer vers la page "Rendez-vous"

### 3. Ouvrir le formulaire
- Cliquer sur le bouton "+ Planifier un Rendez-vous"

### 4. Vérifier le dropdown Patient
- Cliquer sur le champ "Patient"
- **Résultat attendu**: Le dropdown doit afficher les 5 patients:
  - Pierre Durand
  - Marie Dubois
  - Jean Lefebvre
  - Sophie Martin
  - Robert Petit

### 5. Créer un rendez-vous de test
- Sélectionner un patient (ex: Marie Dubois)
- Choisir une date (ex: demain)
- Choisir une heure (ex: 10:00)
- Sélectionner un type (ex: CONSULTATION)
- Cliquer sur "Sauvegarder"

### 6. Vérifier l'affichage
- Le rendez-vous doit apparaître dans le tableau
- Le nom du patient doit être affiché correctement

## Vérification API

### Endpoint utilisé
```
GET http://localhost:8090/api/patients
```

### Test manuel
```powershell
Invoke-RestMethod -Uri "http://localhost:8090/api/patients" -Method GET
```

## Statut
✅ Configuration terminée
✅ Patients créés dans la base de données
✅ Code modifié pour utiliser /api/patients
✅ Angular recompilé avec succès
⏳ Test manuel à effectuer dans le navigateur

## Notes
- Les patients avec `nomComplet` vide sont automatiquement filtrés
- Le code utilise maintenant directement `patientService.getAll()` qui appelle `/api/patients`
- L'objet patient complet est stocké dans le modèle pour faciliter l'affichage du nom
