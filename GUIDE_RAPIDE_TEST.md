# Guide Rapide - Test des Rapports Hebdomadaires Automatiques

## Problème Identifié ✅

Le scheduler fonctionne correctement et s'exécute toutes les minutes, MAIS il y a une erreur:
- Les patients n'ont pas de `soignant_id` assigné
- Erreur: "null id in" lors de la création des rapports

## Solution en 3 Étapes

### ÉTAPE 1: Corriger les données des patients

Ouvrir **phpMyAdmin**: http://localhost/phpmyadmin

1. Sélectionner la base de données `assistancequotidiennedb`
2. Cliquer sur l'onglet "SQL"
3. Copier-coller ce code:

```sql
-- Assigner le soignant (ID 8) aux patients
UPDATE patient SET soignant_id = 8 WHERE id IN (1, 4, 5);

-- Vérifier
SELECT id, nom_complet, soignant_id FROM patient WHERE id IN (1, 4, 5);
```

4. Cliquer sur "Exécuter"

**Résultat attendu**: Les 3 patients ont maintenant `soignant_id = 8`

---

### ÉTAPE 2: Créer des fiches de test pour la semaine actuelle

Dans phpMyAdmin, exécuter ce SQL:

```sql
-- Fiche 1: Patient 1 (Test Patient) - Aujourd'hui
INSERT INTO fiche_transmission (
    patient_id, soignant_id, date_fiche, statut, 
    observance_medicaments_json, alimentation_json,
    commentaire_libre, date_creation, date_envoi
) VALUES (
    1, 8, '2026-04-14', 'envoye',
    '{"totalPris": "3", "totalPrevus": "3"}',
    '{"appetit": "Bon"}',
    'Test Patient - Bonne observance',
    NOW(), NOW()
);

-- Fiche 2: Patient 4 (Jean Dupont) - Aujourd'hui
INSERT INTO fiche_transmission (
    patient_id, soignant_id, date_fiche, statut, 
    observance_medicaments_json, alimentation_json,
    commentaire_libre, date_creation, date_envoi
) VALUES (
    4, 8, '2026-04-14', 'envoye',
    '{"totalPris": "2", "totalPrevus": "3"}',
    '{"appetit": "Moyen"}',
    'Jean Dupont - Un médicament oublié',
    NOW(), NOW()
);

-- Fiche 3: Patient 5 (abdenour) - Aujourd'hui
INSERT INTO fiche_transmission (
    patient_id, soignant_id, date_fiche, statut, 
    observance_medicaments_json, alimentation_json,
    commentaire_libre, date_creation, date_envoi
) VALUES (
    5, 8, '2026-04-14', 'envoye',
    '{"totalPris": "3", "totalPrevus": "3"}',
    '{"appetit": "Bon"}',
    'abdenour - Excellente semaine',
    NOW(), NOW()
);
```

**Résultat attendu**: 3 nouvelles fiches avec statut "envoye"

---

### ÉTAPE 3: Attendre et vérifier les rapports

1. **Attendre 1 minute** (le scheduler s'exécute toutes les minutes)

2. **Vérifier dans les logs Docker**:
```powershell
docker logs --tail 30 assistance-quotidienne
```

Vous devriez voir:
```
🔄 [SCHEDULER] Génération automatique des rapports hebdomadaires
📅 Période: 2026-04-14 → 2026-04-20
📄 Fiches trouvées: 3
👥 Patients concernés: 3
✅ Rapport créé: ID=... Patient=Test Patient
✅ Rapport créé: ID=... Patient=Jean Dupont
✅ Rapport créé: ID=... Patient=abdenour
✅ [SCHEDULER] Rapports générés: 3/3
```

3. **Vérifier dans la base de données**:
```sql
SELECT 
    r.id,
    p.nom_complet as patient,
    r.date_debut,
    r.date_fin,
    r.taux_observance_medicaments,
    r.taux_observance_repas,
    r.envoye_au_medecin,
    r.soignant_id
FROM rapport_hebdomadaire r
JOIN patient p ON r.patient_id = p.id
ORDER BY r.date_creation DESC
LIMIT 5;
```

**Résultat attendu**: 3 nouveaux rapports avec:
- `envoye_au_medecin = 1`
- `soignant_id = 8`
- `date_debut = 2026-04-14`
- `date_fin = 2026-04-20`

4. **Vérifier dans l'interface web**:
   - Ouvrir: http://localhost:4200/soignant-rapports-hebdo
   - Les 3 rapports doivent apparaître dans la section "✅ Envoyés"

---

## Résumé des Résultats Attendus

✅ **Patients corrigés**: 3 patients avec `soignant_id = 8`

✅ **Fiches créées**: 3 fiches avec statut "envoye" pour aujourd'hui

✅ **Rapports générés**: 3 rapports hebdomadaires automatiques

✅ **Visibles sur**: http://localhost:4200/soignant-rapports-hebdo

✅ **Notifications**: 3 notifications envoyées au médecin via WebSocket

---

## Après le Test: Restaurer la Configuration de Production

Une fois le test terminé, il faut restaurer le scheduler pour qu'il s'exécute en production:

1. Modifier le fichier:
   `alzheimer-system-main/backend/assistance quotidienne/src/main/java/assistancequotidienne2/assistancequotidienne2/Services/RapportHebdomadaireScheduler.java`

2. Changer la ligne 42:
```java
// AVANT (Test - toutes les minutes)
@Scheduled(cron = "0 * * * * *")

// APRÈS (Production - tous les lundis à 09:00)
@Scheduled(cron = "0 0 9 * * MON")
```

3. Changer les lignes 52-53:
```java
// AVANT (Test - semaine actuelle)
LocalDate debutSemaineActuelle = aujourdhui.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
LocalDate finSemaineActuelle = debutSemaineActuelle.plusDays(6);

// APRÈS (Production - semaine dernière)
LocalDate debutSemaineDerniere = aujourdhui.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
LocalDate finSemaineDerniere = debutSemaineDerniere.plusDays(6);
```

4. Reconstruire et redémarrer:
```powershell
cd alzheimer-system-main/docker
docker-compose -f docker-compose.yml build assistance-quotidienne
docker-compose -f docker-compose.yml up -d assistance-quotidienne
```

---

## Nettoyage (Optionnel)

Pour supprimer les données de test:

```sql
-- Supprimer les rapports de test
DELETE FROM rapport_hebdomadaire 
WHERE date_debut >= '2026-04-14';

-- Supprimer les fiches de test
DELETE FROM fiche_transmission 
WHERE date_fiche >= '2026-04-14' AND commentaire_libre LIKE '%Test%';
```
