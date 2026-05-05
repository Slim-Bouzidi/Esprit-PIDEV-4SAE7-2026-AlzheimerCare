# 🔐 Keycloak - Informations

## ✅ Keycloak Démarré

**URL**: http://localhost:8081

**Credentials Admin**:
- Username: `admin`
- Password: `admin`

---

## 📊 Statut Actuel

### Keycloak
- ✅ Container démarré: `docker-keycloak-1`
- ✅ Port: 8081
- ✅ Version: 24.0.2
- ✅ Mode: Development

### Guards Angular
- ⚠️ `roleRedirectGuard`: **DÉSACTIVÉ** (retourne toujours `true`)
- ⚠️ `roleGuard`: **DÉSACTIVÉ** (retourne toujours `true`)

---

## 🔧 Configuration Actuelle

### Frontend (Angular)
Les guards Keycloak sont en mode **bypass**:
- Fichier: `role-redirect.guard.ts` → retourne `true`
- Fichier: `role.guard.ts` → retourne `true`
- Résultat: Toutes les routes sont accessibles sans authentification

### Backend
Keycloak est démarré mais l'application fonctionne sans authentification active.

---

## 🎯 Accès à Keycloak

### Admin Console
1. Ouvrir: http://localhost:8081
2. Cliquer sur "Administration Console"
3. Se connecter:
   - Username: `admin`
   - Password: `admin`

### Realms Configurés
Vérifiez dans l'admin console quels realms sont configurés.

---

## 🔄 Réactiver l'Authentification Keycloak

Si vous voulez réactiver l'authentification Keycloak:

### 1. Modifier le Guard de Redirection
Fichier: `alzheimer-system-main/frontend/alzheimer-angular/src/app/core/guards/role-redirect.guard.ts`

Décommenter le code original et supprimer le bypass.

### 2. Modifier le Guard de Rôle
Fichier: `alzheimer-system-main/frontend/alzheimer-angular/src/app/core/guards/role.guard.ts`

Décommenter le code original et supprimer le bypass.

### 3. Vérifier la Configuration Keycloak
Fichier: `alzheimer-system-main/frontend/alzheimer-angular/src/app/keycloak.ts`

Vérifier que l'URL et le realm sont corrects.

---

## 🛑 Arrêter Keycloak

```powershell
docker stop docker-keycloak-1
```

## 🔄 Redémarrer Keycloak

```powershell
docker start docker-keycloak-1
```

## 📋 Logs Keycloak

```powershell
docker logs docker-keycloak-1 -f
```

---

## ⚠️ Note Importante

Actuellement, l'application fonctionne **sans authentification** car les guards sont en mode bypass. Keycloak est démarré mais n'est pas utilisé par l'application.

Pour utiliser Keycloak, il faut:
1. Réactiver les guards
2. Configurer les realms et les utilisateurs dans Keycloak
3. Vérifier la configuration dans `keycloak.ts`

---

**Statut**: ✅ Keycloak démarré mais guards désactivés
**Mode**: Development (bypass authentication)
