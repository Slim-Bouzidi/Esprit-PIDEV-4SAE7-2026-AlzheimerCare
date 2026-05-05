# 📋 Guide: Créer une Fiche de Transmission

## 🎯 Processus Complet

Le système fonctionne exactement comme vous le souhaitez:
1. **Choisir le patient**
2. **Choisir l'horaire** (événement)
3. **Créer la fiche**

---

## 📍 Étape par Étape

### Étape 1: Accéder à l'Agenda
**URL**: http://localhost:4200/soignant-agenda

```
┌─────────────────────────────────────────┐
│ 📅 Agenda Visuel                        │
│ [Semaine] [Jour]    ‹ Semaine du... ›  │
└─────────────────────────────────────────┘
```

---

### Étape 2: Choisir le Patient

Cliquez sur le **chip du patient** en haut de la page:

```
┌─────────────────────────────────────────┐
│ [🌟 Tous] [AA aziz aziz] [AF abdenour] │
└─────────────────────────────────────────┘
          ↑
    Cliquez ici
```

**Résultat**: L'agenda affiche uniquement les événements de ce patient.

---

### Étape 3: Choisir l'Horaire

Cliquez sur un **événement** dans l'agenda:

```
┌──────────────────────┐
│ Saturday 2 May       │
├──────────────────────┤
│ 08:00 Petit-dejeuner │ ← Cliquez ici
│ 09:00 Medicaments    │
│ 12:30 Dejeuner       │
│ 14:00 Medicaments    │
│ 19:00 Diner          │
│ 20:00 Medicaments    │
└──────────────────────┘
```

**Résultat**: Le panneau latéral s'ouvre à droite.

---

### Étape 4: Le Panneau S'Ouvre Automatiquement

```
┌─────────────────────────────────────────────────────────┐
│ Agenda (60%)          │  Panneau Patient (40%)          │
├───────────────────────┼─────────────────────────────────┤
│                       │ 👤 aziz aziz                    │
│ [Événements]          │ 📅 Samedi 2 mai 2026            │
│                       │ ─────────────────────────────── │
│                       │ 📋 Fiche de Transmission        │
│                       │                                 │
│                       │ [Formulaire]                    │
│                       │                                 │
│                       │ [Sauvegarder] [Annuler]        │
└───────────────────────┴─────────────────────────────────┘
```

---

### Étape 5: Remplir la Fiche

Le formulaire contient plusieurs sections:

#### 💊 Observance Médicamenteuse
```
┌─────────────────────────────────────────┐
│ Médicament          Dosage    Pris      │
├─────────────────────────────────────────┤
│ Ebixa              10mg      [✓] Oui    │
│ Donepezil          10mg      [ ] Non    │
│ Aricept            5mg       [✓] Oui    │
└─────────────────────────────────────────┘
```

#### 🍽️ Alimentation & Hydratation
```
┌─────────────────────────────────────────┐
│ Appétit:      [Bon ▼]                   │
│ Hydratation:  [Suffisante ▼]            │
│ Petit-déj:    [Bien mangé]              │
│ Déjeuner:     [Moyen]                   │
│ Dîner:        [Peu mangé]               │
└─────────────────────────────────────────┘
```

#### 🤝 Vie Sociale
```
┌─────────────────────────────────────────┐
│ Interaction:  [Bonne ▼]                 │
│ Hygiène:      [Correcte ▼]              │
│ Sommeil:      [Bon ▼]                   │
│ Humeur:       [Calme ▼]                 │
└─────────────────────────────────────────┘
```

#### 📝 Suivi des Directives
```
┌─────────────────────────────────────────┐
│ Directive              Statut            │
├─────────────────────────────────────────┤
│ Surveillance TA       [Fait ▼]          │
│ Exercices mémoire     [En cours ▼]      │
└─────────────────────────────────────────┘
```

#### 💬 Commentaire Libre
```
┌─────────────────────────────────────────┐
│ [Ajouter vos observations...]           │
│                                         │
│                                         │
└─────────────────────────────────────────┘
```

---

### Étape 6: Signer la Fiche

```
┌─────────────────────────────────────────┐
│ [✓] Signature soignant                  │
│     Signée électroniquement             │
└─────────────────────────────────────────┘
```

**Important**: La fiche doit être signée avant l'envoi au médecin.

---

### Étape 7: Sauvegarder

```
┌─────────────────────────────────────────┐
│ [💾 Sauvegarder]  [❌ Annuler]          │
└─────────────────────────────────────────┘
```

**Résultat**: 
- La fiche est enregistrée dans la base de données
- Visible dans "Gestion des Fiches"
- L'événement peut être marqué comme "fait" (✓)

---

### Étape 8: Envoyer au Médecin (Optionnel)

Depuis la page **"Gestion des Fiches"**:

```
┌─────────────────────────────────────────┐
│ Fiche #123 - aziz aziz - 02/05/2026    │
│ [👁️] [✏️] [📄] [📤 Envoyer] [🗑️]      │
└─────────────────────────────────────────┘
```

Cliquez sur **"📤 Envoyer"**:
- Le médecin reçoit une notification
- Le rapport PDF est généré
- La fiche est marquée comme "envoyée"

---

## 🎬 Exemple Complet

### Scénario: Créer une fiche pour la prise de médicaments de 09:00

1. **Ouvrir l'agenda**: http://localhost:4200/soignant-agenda

2. **Filtrer par patient**: Cliquer sur "AA aziz aziz"

3. **Choisir l'horaire**: Cliquer sur "09:00 Medicaments matin"

4. **Le panneau s'ouvre** automatiquement à droite

5. **Remplir la fiche**:
   - Cocher les médicaments pris
   - Remplir l'alimentation
   - Ajouter des observations

6. **Signer**: Cocher "Signature soignant"

7. **Sauvegarder**: Cliquer sur "💾 Sauvegarder"

8. **Marquer comme fait**: Cliquer sur ✓ sur l'événement

9. **Continuer**: Passer à l'événement suivant

---

## 🔄 Workflow Quotidien

```
Matin:
├─ 08:00 Petit-déjeuner → Créer fiche → Marquer fait
├─ 09:00 Médicaments    → Créer fiche → Marquer fait
└─ 10:00 Toilette       → Créer fiche → Marquer fait

Midi:
├─ 12:30 Déjeuner       → Créer fiche → Marquer fait
└─ 14:00 Médicaments    → Créer fiche → Marquer fait

Après-midi:
└─ 15:00 Activité       → Créer fiche → Marquer fait

Soir:
├─ 19:00 Dîner          → Créer fiche → Marquer fait
├─ 20:00 Médicaments    → Créer fiche → Marquer fait
└─ 21:00 Coucher        → Créer fiche → Marquer fait
```

---

## 💡 Astuces

### Créer Plusieurs Fiches Rapidement
1. Rester en vue "Jour"
2. Cliquer sur un événement
3. Remplir et sauvegarder
4. Le panneau reste ouvert
5. Cliquer sur l'événement suivant
6. Répéter

### Regrouper les Observations
- Créer une fiche en fin de journée
- Regrouper toutes les observations
- Vision globale de la journée

### Utiliser les Filtres
- Filtrer par patient
- Se concentrer sur un patient à la fois
- Éviter la confusion

---

## ⚠️ Points Importants

### La Fiche Doit Être Signée
- Cocher "Signature soignant"
- Obligatoire pour l'envoi au médecin

### Un Événement = Une Fiche
- Chaque événement peut avoir sa propre fiche
- Ou regrouper plusieurs événements dans une fiche

### Sauvegarde Automatique
- Les données sont sauvegardées immédiatement
- Pas de risque de perte

---

## 🎯 Résumé

**3 Clics pour Créer une Fiche**:
1. **Clic 1**: Choisir le patient
2. **Clic 2**: Choisir l'horaire (événement)
3. **Clic 3**: Sauvegarder la fiche

**C'est aussi simple que ça!** 🎉

---

**Date**: 2 mai 2026
**URL Agenda**: http://localhost:4200/soignant-agenda
**URL Fiches**: http://localhost:4200/soignant-fiches
