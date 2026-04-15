# 🧪 Guide de Test Manuel - Via l'Application Web

## Configuration Actuelle
- ✅ Scheduler modifié pour tester la **semaine ACTUELLE** (au lieu de la semaine dernière)
- ✅ Scheduler s'exécute **toutes les minutes** (au lieu de tous les lundis à 09:00)
- ✅ Période testée: **2026-04-14 (Lundi) → 2026-04-20 (Dimanche)**

---

## 📋 ÉTAPES DU TEST

### ÉTAPE 1: Créer des fiches de transmission

1. **Ouvrir l'application soignant**
   - URL: http://localhost:4200/soignant-fiches
   - Vous devriez voir la liste des patients

2. **Créer 3 fiches pour différents patients**

   **Fiche 1 - Patient: Jean Dupont (ID 4)**
   - Cliquer sur le patient "Jean Dupont"
   - Remplir la fiche:
     - Date: Aujourd'hui (2026-04-14)
     - Observance médicaments: Cocher "Tous pris" (3/3)
     - Alimentation: Appétit "Bon"
     - Commentaire: "Patient en bonne forme, excellente observance"
   - Cliquer sur "💾 Enregistrer" (statut: Brouillon)
   - Cliquer sur "📤 Envoyer" (statut: Envoyé)

   **Fiche 2 - Patient: Jean Dupont (ID 4)**
   - Créer une deuxième fiche pour le même patient
   - Remplir la fiche:
     - Date: Aujourd'hui (2026-04-14)
     - Observance médicaments: 2/3 pris
     - Alimentation: Appétit "Moyen"
     - Commentaire: "Patient un peu fatigué"
   - Enregistrer et Envoyer

   **Fiche 3 - Patient: abdenour (ID 5)**
   - Cliquer sur le patient "abdenour"
   - Remplir la fiche:
     - Date: Aujourd'hui (2026-04-14)
     - Observance médicaments: Tous pris (3/3)
     - Alimentation: Appétit "Bon"
     - Commentaire: "Patient très coopératif"
   - Enregistrer et Envoyer

3. **Vérifier que les fiches sont bien envoyées**
   - Statut doit être "✅ Envoyé" (pas "📝 Brouillon")

---

### ÉTAPE 2: Attendre l'exécution du scheduler

⏳ **Attendre 1 minute maximum**

Le scheduler s'exécute automatiquement toutes les minutes et va:
1. Chercher toutes les fiches avec statut "envoye" de la semaine actuelle
2. Grouper les fiches par patient
3. Générer un rapport hebdomadaire pour chaque patient
4. Envoyer une notification au médecin via WebSocket

---

### ÉTAPE 3: Vérifier les logs du scheduler

Ouvrir un terminal et exécuter:

```bash
docker logs assistance-quotidienne --tail 50
```

**Résultat attendu:**
```
🔄 [SCHEDULER] Génération automatique des rapports hebdomadaires - 2026-04-14T...
📅 Période: 2026-04-14 → 2026-04-20
📄 Fiches trouvées: 3
👥 Patients concernés: 2
📊 Stats: Médic=83% Repas=85% RDV=100%
✅ Rapport créé: ID=1 Patient=Jean Dupont
📨 Notification envoyée au médecin via WebSocket
📊 Stats: Médic=100% Repas=100% RDV=100%
✅ Rapport créé: ID=2 Patient=abdenour
📨 Notification envoyée au médecin via WebSocket
✅ [SCHEDULER] Rapports générés: 2/2
```

---

### ÉTAPE 4: Vérifier dans l'interface médecin

1. **Ouvrir la page du médecin**
   - URL: http://localhost:4200/doctor-reports
   - Les rapports doivent apparaître **automatiquement** (via WebSocket)
   - **PAS BESOIN de rafraîchir la page!**

2. **Vérifier les rapports affichés**
   
   Vous devriez voir **2 nouveaux rapports**:
   
   **Rapport 1: Jean Dupont**
   - Période: 2026-04-14 → 2026-04-20
   - Observance médicaments: ~83% (5 pris sur 6 prévus)
   - Observance repas: ~85% (moyenne Bon + Moyen)
   - Observance RDV: 100%
   - Observations: Contient les 2 commentaires des fiches

   **Rapport 2: abdenour**
   - Période: 2026-04-14 → 2026-04-20
   - Observance médicaments: 100% (3/3)
   - Observance repas: 100% (Bon)
   - Observance RDV: 100%
   - Observations: Contient le commentaire de la fiche

3. **Vérifier les notifications**
   - 2 notifications doivent apparaître en temps réel
   - Type: "RAPPORT_HEBDOMADAIRE"
   - Message: "Nouveau rapport hebdomadaire — [Nom du patient]"

---

### ÉTAPE 5: Vérifier dans la base de données (Optionnel)

Ouvrir **phpMyAdmin** (http://localhost/phpmyadmin):

```sql
-- Voir les rapports générés
SELECT 
    r.id,
    p.nom_complet as patient,
    r.date_debut,
    r.date_fin,
    r.taux_observance_medicaments,
    r.taux_observance_repas,
    r.envoye_au_medecin
FROM rapport_hebdomadaire r
JOIN patient p ON r.patient_id = p.id
WHERE r.date_debut = '2026-04-14'
ORDER BY r.date_creation DESC;
```

**Résultat attendu**: 2 lignes (Jean Dupont + abdenour)

---

### ÉTAPE 6: Test de non-duplication

1. **Attendre encore 1 minute** (le scheduler va s'exécuter à nouveau)

2. **Vérifier les logs**
   ```bash
   docker logs assistance-quotidienne --tail 30
   ```

   **Résultat attendu:**
   ```
   🔄 [SCHEDULER] Génération automatique des rapports hebdomadaires
   📅 Période: 2026-04-14 → 2026-04-20
   📄 Fiches trouvées: 3
   👥 Patients concernés: 2
   ⚠️ Rapport déjà existant pour patient 4 période 2026-04-14 → 2026-04-20
   ⚠️ Rapport déjà existant pour patient 5 période 2026-04-14 → 2026-04-20
   ✅ [SCHEDULER] Rapports générés: 0/2
   ```

3. **Vérifier qu'il n'y a pas de doublons**
   ```sql
   SELECT 
       patient_id,
       date_debut,
       COUNT(*) as nombre_rapports
   FROM rapport_hebdomadaire
   WHERE date_debut = '2026-04-14'
   GROUP BY patient_id, date_debut
   HAVING COUNT(*) > 1;
   ```

   **Résultat attendu**: Aucune ligne (pas de doublons)

---

## ✅ Résultats Attendus - Résumé

| Critère | Résultat Attendu |
|---------|------------------|
| **Fiches créées** | 3 fiches avec statut "envoye" |
| **Rapports générés** | 2 rapports (1 par patient) |
| **Période** | 2026-04-14 → 2026-04-20 |
| **Statistiques** | Calculées automatiquement |
| **Notifications** | 2 notifications en temps réel |
| **WebSocket** | Mise à jour automatique sans refresh |
| **Doublons** | Aucun (protection intégrée) |

---

## 🔧 Après le Test

### Restaurer la configuration de production

Une fois le test terminé, il faut restaurer le scheduler pour la production:

1. **Modifier le fichier RapportHebdomadaireScheduler.java**
   - Changer la période: semaine ACTUELLE → semaine DERNIÈRE
   - Changer le cron: toutes les minutes → tous les lundis à 09:00

2. **Reconstruire et redémarrer**
   ```bash
   cd alzheimer-system-main/docker
   docker-compose -f docker-compose.yml build assistance-quotidienne
   docker-compose -f docker-compose.yml up -d assistance-quotidienne
   ```

---

## 🐛 Dépannage

### Problème: Le scheduler ne s'exécute pas
- Vérifier que le service est démarré: `docker ps`
- Vérifier les logs: `docker logs assistance-quotidienne`
- Vérifier que `@EnableScheduling` est présent dans `AssistanceQuotidienne2Application.java`

### Problème: Aucune fiche trouvée
- Vérifier que les fiches ont le statut "envoye" (pas "brouillon")
- Vérifier la date des fiches (doit être dans la semaine actuelle)
- Vérifier dans la base de données:
  ```sql
  SELECT * FROM fiche_transmission 
  WHERE statut = 'envoye' 
  AND date_fiche >= '2026-04-14';
  ```

### Problème: Les rapports n'apparaissent pas dans l'interface médecin
- Vérifier que le WebSocket est connecté (voir console du navigateur)
- Rafraîchir la page manuellement (CTRL + F5)
- Vérifier dans la base de données que les rapports existent

---

## 📝 Notes

- Le test utilise la **semaine actuelle** pour faciliter la création de fiches via l'application
- En production, le scheduler utilisera la **semaine dernière** et s'exécutera **tous les lundis à 09:00**
- Les rapports sont automatiquement marqués comme "envoyés au médecin"
- Le système empêche la création de doublons pour la même période
