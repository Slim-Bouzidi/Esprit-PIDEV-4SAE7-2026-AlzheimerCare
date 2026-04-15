# Test Manuel - Génération Automatique des Rapports Hebdomadaires

## Objectif
Tester la génération automatique des rapports hebdomadaires basés sur les fiches de transmission.

## Prérequis
- Base de données: `assistancequotidiennedb` (XAMPP MySQL)
- Services Docker en cours d'exécution
- Au moins un patient et un soignant dans la base de données

---

## ÉTAPE 1: Vérifier les données existantes

### 1.1 Vérifier les patients
```sql
SELECT id, nom_complet, date_naissance FROM patient;
```

**Résultat attendu**: Liste des patients (IDs: 1, 4, 5)

### 1.2 Vérifier les fiches existantes
```sql
SELECT id, patient_id, date_fiche, statut, date_creation 
FROM fiche_transmission 
ORDER BY date_creation DESC;
```

**Résultat attendu**: Liste des fiches existantes (IDs: 22-26)

---

## ÉTAPE 2: Corriger les patients et créer des fiches de test

### 2.1 Problème identifié
Les patients n'ont pas de `soignant_id` assigné, ce qui cause l'erreur "null id in" lors de la génération des rapports.

### 2.2 Solution: Exécuter le script de correction

**Option A: Via phpMyAdmin (Recommandé)**
1. Ouvrir http://localhost/phpmyadmin
2. Sélectionner la base `assistancequotidiennedb`
3. Aller dans l'onglet "SQL"
4. Copier-coller le contenu du fichier `fix_and_test_rapports.sql`
5. Cliquer sur "Exécuter"

**Option B: Via PowerShell**
```powershell
.\run_sql_fix.ps1
```

**Option C: Via SQL direct (si MySQL est dans le PATH)**

```sql
-- Fiche 1: Patient 4 (Jean Dupont) - Lundi 2026-04-07
INSERT INTO fiche_transmission (
    patient_id, 
    soignant_id, 
    date_fiche, 
    statut, 
    observance_medicaments_json,
    alimentation_json,
    commentaire_libre,
    date_creation,
    date_envoi
) VALUES (
    4,  -- Jean Dupont
    8,  -- Dr. Martin Soignant
    '2026-04-07',
    'envoye',
    '{"totalPris": "3", "totalPrevus": "3", "details": "Tous les médicaments pris"}',
    '{"appetit": "Bon", "quantite": "Normale"}',
    'Patient en bonne forme, bonne observance médicamenteuse',
    '2026-04-07 10:00:00',
    '2026-04-07 10:05:00'
);

-- Fiche 2: Patient 4 (Jean Dupont) - Mercredi 2026-04-09
INSERT INTO fiche_transmission (
    patient_id, 
    soignant_id, 
    date_fiche, 
    statut, 
    observance_medicaments_json,
    alimentation_json,
    commentaire_libre,
    date_creation,
    date_envoi
) VALUES (
    4,
    8,
    '2026-04-09',
    'envoye',
    '{"totalPris": "2", "totalPrevus": "3", "details": "Un médicament oublié"}',
    '{"appetit": "Moyen", "quantite": "Réduite"}',
    'Patient un peu fatigué, a oublié un médicament',
    '2026-04-09 14:00:00',
    '2026-04-09 14:05:00'
);

-- Fiche 3: Patient 5 (abdenour) - Vendredi 2026-04-11
INSERT INTO fiche_transmission (
    patient_id, 
    soignant_id, 
    date_fiche, 
    statut, 
    observance_medicaments_json,
    alimentation_json,
    commentaire_libre,
    date_creation,
    date_envoi
) VALUES (
    5,  -- abdenour
    8,
    '2026-04-11',
    'envoye',
    '{"totalPris": "3", "totalPrevus": "3", "details": "Excellente observance"}',
    '{"appetit": "Bon", "quantite": "Normale"}',
    'Patient très coopératif, excellente semaine',
    '2026-04-11 16:00:00',
    '2026-04-11 16:05:00'
);
```

**Option B: Via l'application web**
1. Aller sur http://localhost:4200/soignant-fiches
2. Créer 3 fiches avec les dates de la semaine dernière
3. Cliquer sur "📤 Envoyer" pour chaque fiche

### 2.3 Vérifier les fiches insérées
```sql
SELECT 
    f.id,
    p.nom_complet as patient,
    f.date_fiche,
    f.statut,
    f.commentaire_libre
FROM fiche_transmission f
JOIN patient p ON f.patient_id = p.id
WHERE f.date_fiche BETWEEN '2026-04-06' AND '2026-04-12'
AND f.statut = 'envoye'
ORDER BY f.date_fiche;
```

**Résultat attendu**: 3 fiches avec statut "envoye" dans la période

---

## ÉTAPE 3: Déclencher le scheduler manuellement

### 3.1 Modifier temporairement le cron pour test immédiat

Le scheduler est configuré pour s'exécuter tous les lundis à 09:00. Pour tester immédiatement, nous allons le modifier pour qu'il s'exécute toutes les minutes.

**Fichier à modifier**: 
`alzheimer-system-main/backend/assistance quotidienne/src/main/java/assistancequotidienne2/assistancequotidienne2/Services/RapportHebdomadaireScheduler.java`

**Ligne à changer**:
```java
// AVANT (Production)
@Scheduled(cron = "0 0 9 * * MON")

// APRÈS (Test - toutes les minutes)
@Scheduled(cron = "0 * * * * *")
```

### 3.2 Reconstruire et redémarrer le service

```bash
cd alzheimer-system-main/docker
docker-compose -f docker-compose.yml build assistance-quotidienne
docker-compose -f docker-compose.yml up -d assistance-quotidienne
```

### 3.3 Surveiller les logs

```bash
docker logs -f assistance-quotidienne
```

**Résultat attendu dans les logs** (après 1 minute):
```
🔄 [SCHEDULER] Génération automatique des rapports hebdomadaires - 2026-04-14T...
📅 Période: 2026-04-06 → 2026-04-12
📄 Fiches trouvées: 3
👥 Patients concernés: 2
✅ Rapport créé: ID=... Patient=Jean Dupont
✅ Rapport créé: ID=... Patient=abdenour
📨 Notification envoyée au médecin via WebSocket
✅ [SCHEDULER] Rapports générés: 2/2
```

---

## ÉTAPE 4: Vérifier les rapports générés

### 4.1 Vérifier dans la base de données

```sql
-- Voir tous les rapports hebdomadaires
SELECT 
    r.id,
    p.nom_complet as patient,
    r.date_debut,
    r.date_fin,
    r.taux_observance_medicaments,
    r.taux_observance_repas,
    r.envoye_au_medecin,
    r.date_creation
FROM rapport_hebdomadaire r
JOIN patient p ON r.patient_id = p.id
ORDER BY r.date_creation DESC;
```

**Résultat attendu**: 2 nouveaux rapports (un pour Jean Dupont, un pour abdenour)

### 4.2 Voir les détails d'un rapport

```sql
SELECT 
    r.*,
    p.nom_complet as patient_nom
FROM rapport_hebdomadaire r
JOIN patient p ON r.patient_id = p.id
WHERE r.id = [ID_DU_RAPPORT]
LIMIT 1;
```

**Vérifications**:
- ✅ `date_debut` = 2026-04-06
- ✅ `date_fin` = 2026-04-12
- ✅ `taux_observance_medicaments` ≈ 88% (pour Jean Dupont: (3+2)/6 * 100)
- ✅ `taux_observance_repas` ≈ 85% (moyenne Bon=100%, Moyen=70%)
- ✅ `envoye_au_medecin` = 1 (true)
- ✅ `observations_generales` contient les commentaires des fiches

### 4.3 Vérifier les notifications

```sql
SELECT 
    n.id,
    n.type,
    n.titre,
    n.message,
    n.reference_id,
    n.date_creation
FROM notification n
WHERE n.type = 'RAPPORT_HEBDOMADAIRE'
ORDER BY n.date_creation DESC;
```

**Résultat attendu**: 2 notifications (une par rapport généré)

---

## ÉTAPE 5: Vérifier dans l'interface web

### 5.1 Page du médecin
1. Ouvrir http://localhost:4200/doctor-reports
2. Vérifier que les nouveaux rapports apparaissent automatiquement (via WebSocket)
3. Vérifier les statistiques affichées

**Résultat attendu**:
- Les rapports s'affichent sans rafraîchir la page
- Les statistiques d'observance sont correctes
- Les observations contiennent les commentaires des fiches

---

## ÉTAPE 6: Restaurer la configuration de production

### 6.1 Remettre le cron de production

**Fichier**: `RapportHebdomadaireScheduler.java`

```java
// Restaurer
@Scheduled(cron = "0 0 9 * * MON")
```

### 6.2 Reconstruire et redémarrer

```bash
cd alzheimer-system-main/docker
docker-compose -f docker-compose.yml build assistance-quotidienne
docker-compose -f docker-compose.yml up -d assistance-quotidienne
```

---

## ÉTAPE 7: Test de non-duplication

### 7.1 Relancer le scheduler (avec cron test)

Attendre 1 minute avec le cron de test activé.

### 7.2 Vérifier qu'aucun doublon n'est créé

```sql
SELECT 
    patient_id,
    date_debut,
    date_fin,
    COUNT(*) as nombre_rapports
FROM rapport_hebdomadaire
WHERE date_debut = '2026-04-06' AND date_fin = '2026-04-12'
GROUP BY patient_id, date_debut, date_fin
HAVING COUNT(*) > 1;
```

**Résultat attendu**: Aucune ligne (pas de doublons)

---

## Résultats Attendus - Résumé

✅ **Fiches créées**: 3 fiches avec statut "envoye" pour la semaine 2026-04-06 → 2026-04-12

✅ **Rapports générés**: 2 rapports hebdomadaires (un par patient)

✅ **Statistiques calculées**: 
- Observance médicaments: ~88% pour Jean Dupont
- Observance repas: ~85% (moyenne)
- Observance RDV: 100%

✅ **Notifications créées**: 2 notifications de type "RAPPORT_HEBDOMADAIRE"

✅ **WebSocket**: Notifications reçues en temps réel par le médecin

✅ **Pas de doublons**: Le scheduler ne crée pas de rapports en double

---

## Nettoyage (Optionnel)

Pour supprimer les données de test:

```sql
-- Supprimer les rapports de test
DELETE FROM rapport_hebdomadaire 
WHERE date_debut = '2026-04-06' AND date_fin = '2026-04-12';

-- Supprimer les notifications de test
DELETE FROM notification 
WHERE type = 'RAPPORT_HEBDOMADAIRE' 
AND date_creation >= '2026-04-14';

-- Supprimer les fiches de test
DELETE FROM fiche_transmission 
WHERE date_fiche BETWEEN '2026-04-06' AND '2026-04-12';
```

---

## Notes Importantes

1. **Période calculée**: Le scheduler calcule automatiquement la semaine dernière (lundi → dimanche)

2. **Statut des fiches**: Seules les fiches avec statut "envoye" sont prises en compte

3. **Groupement par patient**: Un rapport est créé par patient ayant des fiches dans la période

4. **Envoi automatique**: Les rapports sont automatiquement marqués comme envoyés au médecin

5. **WebSocket**: Les notifications sont envoyées en temps réel sans rafraîchissement de page

6. **Production**: En production, le scheduler s'exécute tous les lundis à 09:00
