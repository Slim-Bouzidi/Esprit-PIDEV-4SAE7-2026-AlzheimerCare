# 📅 Générer des Événements d'Agenda pour le Soignant

## 🎯 Problème Actuel

L'agenda du soignant affiche **"Aucun événement prévu"** car il n'y a pas d'événements créés dans la base de données.

## ✅ Solution: Générer des Événements Automatiquement

Le backend a un endpoint spécial pour générer automatiquement des événements d'agenda pour tous les patients.

### 📍 Endpoint API

```
POST http://localhost:8098/api/evenements-agenda/generer
```

### 🔧 Ce que cet endpoint fait:

Pour chaque patient dans la base de données, il crée automatiquement des événements quotidiens:

1. **08:00** - Petit-déjeuner
2. **09:00** - Prise de médicaments (matin)
3. **12:00** - Déjeuner
4. **14:00** - Prise de médicaments (après-midi)
5. **18:00** - Dîner
6. **20:00** - Prise de médicaments (soir)
7. **21:00** - Coucher

### 📅 Période Générée

- **7 jours** à partir d'aujourd'hui
- Événements créés pour **tous les patients** existants

---

## 🚀 Comment Générer les Événements

### Option 1: Via PowerShell (Recommandé)

Exécutez le script `generer-evenements-agenda.ps1`:

```powershell
./generer-evenements-agenda.ps1
```

### Option 2: Via curl

```bash
curl -X POST http://localhost:8098/api/evenements-agenda/generer
```

### Option 3: Via Postman/Insomnia

- **Method**: POST
- **URL**: http://localhost:8098/api/evenements-agenda/generer
- **Headers**: Aucun requis
- **Body**: Aucun requis

---

## 📋 Après Génération

Une fois les événements générés:

1. **Rafraîchir la page** de l'agenda soignant
2. Vous verrez les **créneaux horaires** pour chaque jour
3. **Cliquer sur un événement** pour:
   - Voir les détails
   - **Créer une fiche de transmission**
   - Marquer comme fait/en retard/annulé

---

## 🎯 Workflow Complet

### 1. Générer les Événements
```powershell
./generer-evenements-agenda.ps1
```

### 2. Accéder à l'Agenda
http://localhost:4200/soignant-agenda

### 3. Voir les Événements
- Vue **Semaine** ou **Jour**
- Filtrer par **patient**
- Événements affichés avec **heure** et **type**

### 4. Créer une Fiche
- **Cliquer sur un événement** (ex: "Prise de médicaments 09:00")
- Un **panneau latéral** s'ouvre
- **Remplir la fiche de transmission**:
  - Observance médicamenteuse
  - Alimentation
  - Vie sociale
  - Commentaires
- **Signer** la fiche
- **Envoyer au médecin**

---

## 📊 Types d'Événements Générés

| Heure | Type | Titre | Détails |
|-------|------|-------|---------|
| 08:00 | repas | Petit-déjeuner | Surveiller l'appétit et l'hydratation |
| 09:00 | medicament | Prise de médicaments | Médicaments du matin |
| 12:00 | repas | Déjeuner | Surveiller l'appétit et l'hydratation |
| 14:00 | medicament | Prise de médicaments | Médicaments de l'après-midi |
| 18:00 | repas | Dîner | Surveiller l'appétit et l'hydratation |
| 20:00 | medicament | Prise de médicaments | Médicaments du soir |
| 21:00 | soin | Coucher | Vérifier le confort et la sécurité |

---

## ⚠️ Notes Importantes

### Événements Uniques
Le système vérifie si un événement existe déjà avant de le créer. Vous pouvez exécuter le script plusieurs fois sans créer de doublons.

### Patients Requis
Les événements sont créés **uniquement pour les patients existants** dans la base de données. Assurez-vous d'avoir des patients créés.

### Statut Initial
Tous les événements sont créés avec le statut **"prevu"** (prévu).

---

## 🔄 Régénérer les Événements

Si vous voulez régénérer les événements:

1. **Supprimer les anciens** (optionnel):
   ```sql
   DELETE FROM evenement_agenda;
   ```

2. **Régénérer**:
   ```powershell
   ./generer-evenements-agenda.ps1
   ```

---

## 🎉 Résultat Attendu

Après génération, l'agenda affichera:

```
Monday 27 Apr
├─ 08:00 🍽️ Petit-déjeuner (aziz aziz)
├─ 09:00 💊 Prise de médicaments (aziz aziz)
├─ 12:00 🍽️ Déjeuner (aziz aziz)
├─ 14:00 💊 Prise de médicaments (aziz aziz)
├─ 18:00 🍽️ Dîner (aziz aziz)
├─ 20:00 💊 Prise de médicaments (aziz aziz)
└─ 21:00 🛏️ Coucher (aziz aziz)

Tuesday 28 Apr
├─ 08:00 🍽️ Petit-déjeuner (aziz aziz)
...
```

---

**Date**: 2 mai 2026
**Statut**: ✅ PRÊT À GÉNÉRER
**Script**: `generer-evenements-agenda.ps1`
