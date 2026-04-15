# 🧪 Test Envoi WebSocket - Guide Complet

## 📋 Préparation

### Étape 1: Créer une fiche NON envoyée dans XAMPP

1. Ouvre **XAMPP**
2. Clique sur **Admin** pour MySQL (phpMyAdmin)
3. Sélectionne la base de données **`assistancequotidiennedb`**
4. Clique sur l'onglet **SQL**
5. Copie et exécute cette requête:

```sql
INSERT INTO fiche_transmission 
(patient_id, soignant_id, date_fiche, date_creation, statut, signature_soignant, commentaire_libre) 
VALUES 
(1, 8, '2026-04-14', NOW(), 'brouillon', 1, 'TEST WEBSOCKET - Fiche à envoyer');
```

6. Clique sur **Exécuter**
7. Note l'ID de la fiche créée (par exemple: 13)

---

## 🎯 Test Complet WebSocket

### Étape 2: Ouvrir 2 fenêtres de navigateur

**Fenêtre 1 - MÉDECIN (Récepteur):**
```
http://localhost:4200/doctor-reports
```
- Ouvre la console du navigateur (F12 → Console)
- Tu devrais voir des messages de connexion WebSocket

**Fenêtre 2 - SOIGNANT (Émetteur):**
```
http://localhost:4200/soignant-fiches
```
- Recharge la page (F5) pour voir la nouvelle fiche

### Étape 3: Dans la fenêtre SOIGNANT

1. Tu devrais voir une fiche avec:
   - Statut: **Brouillon** (badge jaune)
   - Signature: **✓ Signée**
   - Bouton: **📤 Envoyer** (bleu)

2. Clique sur le bouton **"📤 Envoyer"**

3. Une modal de confirmation s'ouvre avec:
   - Résumé de la fiche
   - Patient
   - Date
   - Statut
   - Signature

4. Clique sur **"📤 Confirmer l'envoi"**

### Étape 4: Observer les résultats

**Dans la fenêtre SOIGNANT:**
- ✅ Le bouton change en **"✅ Envoyé"** (vert, désactivé)
- ✅ Le statut passe à **"Envoyée"** (badge vert)
- ✅ Un toast de succès apparaît en bas à droite
- ✅ Pas besoin de recharger la page!

**Dans la fenêtre MÉDECIN:**
- ✅ La console affiche un message de notification WebSocket
- ✅ La cloche de notification 🔔 s'anime
- ✅ Une nouvelle notification apparaît
- ✅ Le rapport est visible dans la liste

**Dans la console du MÉDECIN, tu devrais voir:**
```javascript
Received notification: {
  notificationId: 123,
  destinataireId: 8,
  type: "FICHE_ENVOYEE",
  titre: "Rapport hebdomadaire envoyé — ...",
  message: "Le soignant a validé et envoyé la fiche...",
  referenceType: "FICHE_TRANSMISSION",
  referenceId: 13,
  dateCreation: "2026-04-14T..."
}
```

---

## ✅ Résultat attendu

### Temps de réponse:
- **< 1 seconde** entre le clic sur "Envoyer" et la réception de la notification

### Flux complet:
```
SOIGNANT clique "Envoyer"
    ↓
Backend reçoit PATCH /api/fiches/{id}/envoyer
    ↓
Backend met à jour statut = "envoye"
    ↓
Backend crée une notification
    ↓
Backend envoie via WebSocket (/topic/doctor-notifications)
    ↓
MÉDECIN reçoit la notification en temps réel
    ↓
Frontend affiche la notification
```

---

## 🐛 Dépannage

### Si le bouton "📤 Envoyer" n'apparaît pas:

1. Vérifie que la fiche a le statut "brouillon" dans la base de données
2. Recharge la page (F5)
3. Vérifie les filtres (Statut = "Tous")

### Si le bouton ne change pas après le clic:

1. Ouvre la console (F12)
2. Cherche des erreurs
3. Vérifie que le backend est lancé (port 8098)

### Si la notification n'arrive pas au médecin:

1. Vérifie la console du médecin pour des erreurs WebSocket
2. Vérifie que le backend est lancé
3. Vérifie que le patient a un soignant (médecin) assigné

### Vérifier la connexion WebSocket:

1. Ouvre la console (F12) dans la fenêtre médecin
2. Va dans l'onglet **Network** (Réseau)
3. Filtre par **WS** (WebSocket)
4. Tu devrais voir: `ws://localhost:8098/ws`
5. Status: **101 Switching Protocols**

---

## 📊 Vérification dans la base de données

Après l'envoi, vérifie dans phpMyAdmin:

**Table `fiche_transmission`:**
```sql
SELECT id, statut, date_envoi, signature_soignant 
FROM fiche_transmission 
WHERE id = 13;
```

Résultat attendu:
- statut = "envoye"
- date_envoi = (date et heure actuelles)
- signature_soignant = 1

**Table `notification`:**
```sql
SELECT * FROM notification 
WHERE reference_type = 'FICHE_TRANSMISSION' 
AND reference_id = 13;
```

Résultat attendu:
- Une notification créée
- type = "FICHE_ENVOYEE"
- destinataire_id = 8 (le médecin)

---

## 🎉 Test réussi si:

- ✅ Le bouton change de "📤 Envoyer" à "✅ Envoyé" sans recharger
- ✅ Le médecin reçoit la notification en moins de 1 seconde
- ✅ La notification apparaît dans la console du médecin
- ✅ Le toast de succès s'affiche dans la fenêtre soignant

Bonne chance! 🚀
