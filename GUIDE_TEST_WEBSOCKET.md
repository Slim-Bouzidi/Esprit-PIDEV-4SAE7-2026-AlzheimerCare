# 🧪 Guide de Test WebSocket - Application Alzheimer

## 📋 WebSocket existe entre:

**SOIGNANT → MÉDECIN** (Notifications en temps réel)

Quand un soignant envoie une fiche de transmission au médecin, le médecin reçoit une notification instantanée via WebSocket.

---

## 🎯 Test Complet - Étape par Étape

### Préparation

1. ✅ Backend lancé (port 8098)
2. ✅ Frontend lancé (port 4200)
3. ✅ Base de données avec:
   - Patient ID 1 (ou autre)
   - Soignant ID 8 (Dr. Martin Soignant)
   - Médecin ID 8 (même utilisateur peut être médecin et soignant)

---

### 🧪 TEST 1: Notification via Fiche de Transmission

#### Étape 1: Ouvre 2 fenêtres de navigateur côte à côte

**Fenêtre A - MÉDECIN (Récepteur):**
```
http://localhost:4200/doctor-reports
```
- Ouvre la console du navigateur (F12 → Console)
- Tu devrais voir des logs de connexion WebSocket

**Fenêtre B - SOIGNANT (Émetteur):**
```
http://localhost:4200/soignant-fiches
```

#### Étape 2: Dans la fenêtre MÉDECIN (A)

1. Regarde la console (F12)
2. Cherche des messages comme:
   - "WebSocket connected" ou similaire
   - Connexion à `/ws`
   - Abonnement à `/topic/doctor-notifications`

#### Étape 3: Dans la fenêtre SOIGNANT (B)

1. Va sur la page "Fiches de transmission"
2. Clique sur "Créer depuis l'agenda" ou crée une nouvelle fiche
3. Remplis les informations:
   - Patient: Sélectionne un patient
   - Observations: "Test WebSocket - Notification temps réel"
   - Prises médicaments: Oui
   - Repas: Oui
   - État général: STABLE
4. Sauvegarde la fiche
5. **Clique sur le bouton "📤 Envoyer"** dans le tableau

#### Étape 4: Observe dans la fenêtre MÉDECIN (A)

**Ce qui devrait se passer:**

✅ **Dans la console:**
- Un message apparaît avec les détails de la notification
- Type: "FICHE_TRANSMISSION"
- Contenu de la notification

✅ **Dans l'interface:**
- La cloche de notification 🔔 devrait s'animer ou afficher un badge
- Un nouveau rapport devrait apparaître dans la liste

---

### 🧪 TEST 2: Notification via Rapport Hebdomadaire

#### Dans la fenêtre SOIGNANT (B)

1. Va sur la page "Rapports" (`/soignant-rapports`)
2. Dans la section "Rapports Hebdomadaires"
3. Trouve un rapport non envoyé
4. Clique sur "Envoyer au médecin"

#### Observe dans la fenêtre MÉDECIN (A)

✅ Une notification devrait arriver en temps réel
- Type: "RAPPORT_HEBDOMADAIRE"

---

## 🔍 Vérifications Techniques

### Dans la console du navigateur (Médecin)

Tu devrais voir:
```
WebSocket connection established
Subscribed to /topic/doctor-notifications
```

Quand une notification arrive:
```
Received notification: {
  notificationId: 123,
  type: "FICHE_TRANSMISSION",
  titre: "Nouvelle fiche de transmission",
  message: "...",
  ...
}
```

### Vérifier la connexion WebSocket

1. Ouvre la console (F12)
2. Va dans l'onglet "Network" (Réseau)
3. Filtre par "WS" (WebSocket)
4. Tu devrais voir une connexion à: `ws://localhost:8098/ws`
5. Status: "101 Switching Protocols" (connexion établie)

---

## ❌ Problèmes possibles

### Si aucune notification n'arrive:

1. **Vérifie la console du médecin:**
   - Y a-t-il des erreurs WebSocket?
   - La connexion est-elle établie?

2. **Vérifie le backend:**
   - Le service `assistance-quotidienne` est-il lancé?
   - Port 8098 accessible?

3. **Vérifie la base de données:**
   - La fiche a-t-elle été créée?
   - La notification a-t-elle été créée dans la table `notification`?

4. **Vérifie le code:**
   - `WebSocketConfig.java` - Endpoint `/ws` configuré?
   - `NotificationWsService.java` - Méthode `notifyDoctor()` appelée?

---

## 📊 Résultat attendu

✅ **Test réussi si:**
- Le médecin reçoit la notification en moins de 1 seconde
- La notification apparaît dans la console
- La cloche de notification s'anime
- Le rapport apparaît dans la liste

❌ **Test échoué si:**
- Aucune notification n'arrive après 5 secondes
- Erreurs dans la console
- WebSocket déconnecté

---

## 🎓 Comprendre le flux

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│  SOIGNANT   │         │   BACKEND   │         │   MÉDECIN   │
│  (Angular)  │         │  (Spring)   │         │  (Angular)  │
└──────┬──────┘         └──────┬──────┘         └──────┬──────┘
       │                       │                       │
       │ 1. POST /fiches       │                       │
       │──────────────────────>│                       │
       │                       │                       │
       │ 2. PATCH /envoyer     │                       │
       │──────────────────────>│                       │
       │                       │                       │
       │                       │ 3. WebSocket Push     │
       │                       │──────────────────────>│
       │                       │   /topic/doctor-      │
       │                       │   notifications       │
       │                       │                       │
       │                       │                  4. 🔔 Affiche
       │                       │                     notification
```

---

## 🚀 Commencer le test

1. Assure-toi que tout est lancé
2. Ouvre les 2 fenêtres
3. Suis les étapes du TEST 1
4. Observe les résultats

Bonne chance! 🎉
