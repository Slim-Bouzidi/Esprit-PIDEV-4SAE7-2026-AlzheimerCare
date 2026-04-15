# 🔧 Dépannage - Problème de Redirection

## ❌ Problème Actuel
L'URL `http://localhost:4200/doctor-memoire-assistee` redirige vers `/admin/manage-users`

---

## ✅ Solutions par Ordre de Priorité

### 🎯 Solution 1: Vérifier l'URL (CRITIQUE!)

**Vous avez écrit**: `http://localhost:4200/doctor-memoire-assisteea`
**URL correcte**: `http://localhost:4200/doctor-memoire-assistee`

⚠️ **Notez le "a" en trop à la fin!** C'est probablement la cause du problème.

---

### 🔄 Solution 2: Vider le Cache du Navigateur

Le navigateur a peut-être mis en cache l'ancien guard qui redirige.

**Méthode rapide:**
1. Appuyer sur `Ctrl + Shift + Delete`
2. Cocher "Images et fichiers en cache"
3. Cliquer sur "Effacer les données"
4. Fermer et rouvrir le navigateur

**OU simplement:**
- Appuyer sur `Ctrl + F5` pour rafraîchir sans cache

---

### 🕵️ Solution 3: Navigation Privée

Testez en navigation privée pour éviter tout cache:

1. Ouvrir une fenêtre privée:
   - Chrome: `Ctrl + Shift + N`
   - Firefox: `Ctrl + Shift + P`
   - Edge: `Ctrl + Shift + N`

2. Aller sur: `http://localhost:4200/doctor-memoire-assistee`

Si ça fonctionne en navigation privée, c'est un problème de cache!

---

### 🎨 Solution 4: Accéder via le Menu

Au lieu d'accéder directement par l'URL:

1. Aller sur: `http://localhost:4200/doctor-dashboard`
2. Dans le menu latéral gauche, chercher: **🧠 Mémoire Assistée**
3. Cliquer dessus

---

### 🔄 Solution 5: Redémarrer le Frontend (RECOMMANDÉ)

Le cache Angular peut être le problème.

**Option A: Utiliser le script PowerShell**
```powershell
.\restart-frontend.ps1
```

**Option B: Manuellement**
```powershell
# 1. Arrêter le serveur Angular (Ctrl+C dans le terminal)

# 2. Supprimer le cache
Remove-Item -Recurse -Force "alzheimer-system-main/frontend/alzheimer-angular/.angular"

# 3. Redémarrer
cd alzheimer-system-main/frontend/alzheimer-angular
npm start
```

Attendez que le serveur démarre complètement (message "Compiled successfully").

Puis testez: `http://localhost:4200/doctor-memoire-assistee`

---

### 🔍 Solution 6: Vérifier la Console du Navigateur

Ouvrez la console (F12) et regardez:

**Onglet Console:**
- Cherchez: `[RoleRedirect] Keycloak disabled - allowing direct navigation`
- Si vous voyez ce message, le guard fonctionne correctement
- S'il y a des erreurs en rouge, notez-les

**Onglet Network:**
- Rafraîchissez la page
- Regardez quelle URL est appelée
- Y a-t-il une redirection (code 301/302)?

---

### 🛠️ Solution 7: Vérifier le Routing

Vérifiez que la route existe bien:

```powershell
# Chercher la route dans le fichier
Select-String -Path "alzheimer-system-main/frontend/alzheimer-angular/src/app/app-routing.module.ts" -Pattern "doctor-memoire-assistee"
```

Vous devriez voir:
```
{ path: 'doctor-memoire-assistee', component: DoctorMemoireAssisteeComponent, ...
```

---

## 🧪 Test de Diagnostic

Essayez ces URLs une par une et notez le résultat:

1. `http://localhost:4200/` → Devrait aller quelque part
2. `http://localhost:4200/doctor-dashboard` → Dashboard médecin
3. `http://localhost:4200/doctor-patients` → Liste des patients
4. `http://localhost:4200/doctor-memoire-assistee` → Page Mémoire Assistée

Si les 3 premières fonctionnent mais pas la 4ème, c'est un problème spécifique à cette route.

---

## 🎯 Procédure Complète Recommandée

Suivez ces étapes dans l'ordre:

### Étape 1: Vérifier l'URL
✅ Utilisez: `http://localhost:4200/doctor-memoire-assistee` (sans "a" à la fin)

### Étape 2: Vider le cache
✅ `Ctrl + Shift + Delete` → Effacer les données

### Étape 3: Redémarrer le frontend
```powershell
# Dans le terminal où Angular tourne
Ctrl + C

# Supprimer le cache
cd alzheimer-system-main/frontend/alzheimer-angular
Remove-Item -Recurse -Force .angular

# Redémarrer
npm start
```

### Étape 4: Tester en navigation privée
✅ `Ctrl + Shift + N` → `http://localhost:4200/doctor-memoire-assistee`

### Étape 5: Tester via le menu
✅ Dashboard → Cliquer sur "🧠 Mémoire Assistée"

---

## 📊 Diagnostic Avancé

Si rien ne fonctionne, vérifiez:

### 1. Le guard est-il bien modifié?
```powershell
Select-String -Path "alzheimer-system-main/frontend/alzheimer-angular/src/app/core/guards/role-redirect.guard.ts" -Pattern "return true"
```

Devrait afficher: `return true;`

### 2. Le composant existe-t-il?
```powershell
Test-Path "alzheimer-system-main/frontend/alzheimer-angular/src/app/doctor/doctor-memoire-assistee.component.ts"
```

Devrait afficher: `True`

### 3. Y a-t-il des erreurs de compilation?
Regardez le terminal où Angular tourne. Y a-t-il des erreurs en rouge?

---

## 🆘 Si Rien ne Fonctionne

Il se peut qu'il y ait un autre guard ou une autre logique de redirection.

Envoyez-moi:
1. Le message dans la console du navigateur (F12)
2. Le contenu de l'onglet Network quand vous accédez à l'URL
3. Les erreurs éventuelles dans le terminal Angular

---

## ✅ Résultat Attendu

Quand tout fonctionne, vous devriez voir:

1. **URL dans le navigateur**: `http://localhost:4200/doctor-memoire-assistee`
2. **Page affichée**: Interface "Mémoire Assistée" avec:
   - Titre "Mémoire Assistée"
   - Dropdown de sélection de patient
   - 3 sections: Infos personnelles, Album photos, Notes
3. **Console du navigateur**: `[RoleRedirect] Keycloak disabled - allowing direct navigation`

---

**Prochaine étape**: Essayez d'abord avec l'URL correcte (sans le "a" en trop), puis videz le cache!
