# 📅 Guide Complet de l'Agenda Soignant

## 🎯 Vue d'Ensemble

L'agenda soignant permet de:
- ✅ Visualiser les événements quotidiens (repas, médicaments, soins)
- ✅ Créer des fiches de transmission
- ✅ Marquer les tâches comme effectuées
- ✅ Filtrer par patient
- ✅ Naviguer entre les jours et semaines

---

## 📍 Accès

**URL**: http://localhost:4200/soignant-agenda

---

## 🎨 Interface de l'Agenda

### 1. En-tête
```
┌─────────────────────────────────────────────┐
│ 📅 Agenda Visuel                            │
│ Gérez les tâches et rendez-vous            │
│                                             │
│ [Semaine] [Jour]    ‹ Semaine du... ›     │
└─────────────────────────────────────────────┘
```

### 2. Sélecteur de Patients
```
┌─────────────────────────────────────────────┐
│ [🌟 Tous les patients] [AA aziz aziz] ...  │
└─────────────────────────────────────────────┘
```

### 3. Vue Semaine (60% gauche)
```
┌──────────┬──────────┬──────────┬──────────┐
│ Monday   │ Tuesday  │ Wednesday│ Thursday │
│ 27 Apr   │ 28 Apr   │ 29 Apr   │ 30 Apr   │
├──────────┼──────────┼──────────┼──────────┤
│ 08:00    │ 08:00    │ 08:00    │ 08:00    │
│ Petit-   │ Petit-   │ Petit-   │ Petit-   │
│ déjeuner │ déjeuner │ déjeuner │ déjeuner │
│ [✓]      │ [✓]      │ [✓]      │ [✓]      │
├──────────┼──────────┼──────────┼──────────┤
│ 09:00    │ 09:00    │ 09:00    │ 09:00    │
│ Prise de │ Prise de │ Prise de │ Prise de │
│ médica...│ médica...│ médica...│ médica...│
│ [✓]      │ [✓]      │ [✓]      │ [✓]      │
└──────────┴──────────┴──────────┴──────────┘
```

### 4. Panneau Patient (40% droite)
```
┌─────────────────────────────────┐
│ 👤 aziz aziz                    │
│ ─────────────────────────────── │
│ 📋 Créer Fiche de Transmission  │
│                                 │
│ [Formulaire de fiche]           │
│                                 │
│ [Sauvegarder] [Annuler]        │
└─────────────────────────────────┘
```

---

## 🎯 Fonctionnalités Principales

### 1. Changer de Vue

#### Vue Semaine
- Cliquez sur **"Semaine"**
- Affiche 7 jours côte à côte
- Vue d'ensemble de la semaine
- Événements compacts

#### Vue Jour
- Cliquez sur **"Jour"**
- Affiche un seul jour en détail
- Événements avec plus d'informations
- Meilleur pour le suivi quotidien

### 2. Navigation

#### Naviguer dans le Temps
- **‹** (Flèche gauche): Semaine/Jour précédent
- **›** (Flèche droite): Semaine/Jour suivant
- **Label central**: Affiche la période actuelle

### 3. Filtrer par Patient

#### Tous les Patients
- Cliquez sur **"🌟 Tous les patients"**
- Affiche les événements de tous les patients

#### Patient Spécifique
- Cliquez sur un **chip patient** (ex: "AA aziz aziz")
- Affiche uniquement les événements de ce patient
- Le chip devient actif (surligné)

### 4. Interagir avec les Événements

#### Marquer comme Fait
- Cliquez sur le bouton **✓** sur un événement
- L'événement change de couleur (vert)
- Statut passe à "fait"

#### Ouvrir la Fiche de Transmission
- **Cliquez sur l'événement** (pas sur le ✓)
- Le panneau latéral s'ouvre à droite
- Formulaire de fiche de transmission affiché

---

## 📋 Créer une Fiche de Transmission

### Étape 1: Cliquer sur un Événement
- Cliquez sur n'importe quel événement (ex: "Prise de médicaments 09:00")
- Le panneau latéral s'ouvre automatiquement

### Étape 2: Remplir la Fiche

#### 💊 Observance Médicamenteuse
- Liste des médicaments du patient
- Cocher "Pris" ou "Non pris"
- Ajouter des commentaires

#### 🍽️ Alimentation & Hydratation
- Appétit: Bon / Moyen / Faible
- Hydratation: Suffisante / Insuffisante
- Détails des repas

#### 🤝 Vie Sociale
- Interaction sociale
- Hygiène
- Sommeil
- Humeur

#### 📝 Suivi des Directives
- Directives médicales à suivre
- Statut: Fait / Non fait / En cours

#### 💬 Commentaire Libre
- Observations générales
- Incidents
- Remarques importantes

### Étape 3: Signer la Fiche
- Cocher **"Signature soignant"**
- La fiche est maintenant signée électroniquement

### Étape 4: Sauvegarder
- Cliquer sur **"Sauvegarder"**
- La fiche est enregistrée
- Visible dans "Gestion des Fiches"

### Étape 5: Envoyer au Médecin (Optionnel)
- Depuis "Gestion des Fiches"
- Cliquer sur **"📤 Envoyer au médecin"**
- Le médecin reçoit une notification

---

## 🎨 Codes Couleurs des Événements

### Par Type
- **🍽️ Orange**: Repas (petit-déjeuner, déjeuner, dîner)
- **💊 Vert**: Médicaments (prises de médicaments)
- **🏥 Bleu**: Soins (kinésithérapie, toilette, etc.)
- **📅 Violet**: Rendez-vous (médecin, spécialiste)

### Par Statut
- **Gris clair**: En attente (pas encore fait)
- **Vert**: Fait (✓ marqué comme effectué)
- **Rouge**: En retard (heure passée, pas fait)
- **Jaune**: Annulé

---

## 🔄 Workflow Complet

```
1. Ouvrir l'agenda
   ↓
2. Choisir la vue (Semaine/Jour)
   ↓
3. Filtrer par patient (optionnel)
   ↓
4. Cliquer sur un événement
   ↓
5. Panneau latéral s'ouvre
   ↓
6. Remplir la fiche de transmission
   ↓
7. Signer la fiche
   ↓
8. Sauvegarder
   ↓
9. Marquer l'événement comme fait (✓)
   ↓
10. Passer à l'événement suivant
```

---

## 💡 Conseils d'Utilisation

### Pour une Meilleure Organisation

1. **Commencer par la vue Semaine**
   - Vue d'ensemble de la charge de travail
   - Identifier les jours chargés

2. **Filtrer par patient**
   - Se concentrer sur un patient à la fois
   - Éviter la confusion

3. **Utiliser la vue Jour pour le suivi**
   - Plus de détails
   - Meilleur pour remplir les fiches

4. **Marquer les tâches au fur et à mesure**
   - Cliquer sur ✓ après chaque tâche
   - Suivi en temps réel

5. **Créer les fiches en fin de journée**
   - Regrouper les observations
   - Vision globale de la journée

---

## 🚀 Raccourcis Clavier (À venir)

- **S**: Basculer en vue Semaine
- **J**: Basculer en vue Jour
- **←**: Période précédente
- **→**: Période suivante
- **Espace**: Marquer comme fait

---

## ⚠️ Points d'Attention

### Événements sans Heure
Si un événement affiche "00:00", c'est un problème de format d'heure dans la base de données. Contactez l'administrateur.

### Panneau qui ne s'ouvre pas
- Vérifiez que vous cliquez sur l'événement (pas sur le ✓)
- Rafraîchissez la page (F5)
- Vérifiez la console du navigateur (F12)

### Événements en Double
Si vous voyez des événements en double, c'est peut-être dû à:
- Génération automatique depuis les traitements
- Création manuelle d'événements
- Utilisez le script de suppression pour nettoyer

---

## 📞 Support

Pour toute question ou problème:
1. Vérifiez ce guide
2. Consultez "Gestion des Fiches" pour voir les fiches créées
3. Contactez l'administrateur système

---

**Date**: 2 mai 2026
**Version**: 1.0
**URL**: http://localhost:4200/soignant-agenda
