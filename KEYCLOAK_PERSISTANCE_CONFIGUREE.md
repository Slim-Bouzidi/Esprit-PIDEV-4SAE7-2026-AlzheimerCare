# ✅ Keycloak avec Persistance PostgreSQL Configuré

## 🎉 Configuration Réussie!

Keycloak est maintenant configuré avec **PostgreSQL** pour la **persistance des données**.

### ✅ Avantages

- ✅ **Toutes les données sont persistées** dans un volume Docker
- ✅ **Votre realm reste enregistré** après chaque redémarrage
- ✅ **Tous les utilisateurs et clients** sont conservés
- ✅ **Configuration permanente**

---

## 🗄️ Architecture de Persistance

### Base de Données PostgreSQL
- **Image**: `postgres:15-alpine`
- **Container**: `postgres-keycloak`
- **Database**: `keycloak`
- **User**: `keycloak`
- **Password**: `keycloak`
- **Volume**: `keycloak-postgres-data` (persistant)

### Keycloak
- **Image**: `quay.io/keycloak/keycloak:26.2.3`
- **Container**: `keycloak`
- **Port**: 8081
- **Database**: PostgreSQL (au lieu de dev-mem)

---

## 🌐 Accès Keycloak

**URL**: http://localhost:8081

**Identifiants Admin**:
- Username: `admin`
- Password: `admin`

**Console d'Administration**: http://localhost:8081/admin

---

## 📋 Services Actifs

| Service | Container | Port | Statut |
|---------|-----------|------|--------|
| **Keycloak** | keycloak | 8081 | ✅ Running (healthy) |
| **PostgreSQL Keycloak** | postgres-keycloak | 5432 | ✅ Running (healthy) |
| **Discovery Server** | discovery-server | 8761 | ✅ Running (healthy) |
| **API Gateway** | api-gateway | 8090 | ✅ Running |
| **Assistance Quotidienne** | assistance-quotidienne | 8098 | ✅ Running |
| **RabbitMQ** | rabbitmq | 5672, 15672 | ✅ Running (healthy) |
| **Angular Frontend** | - | 4200 | ✅ Running |

---

## 🔧 Créer votre Realm

Maintenant vous pouvez créer votre realm `alzheimer-realm` et il sera **conservé définitivement**!

### Étapes:

1. **Accéder à Keycloak**: http://localhost:8081
2. **Se connecter**: admin / admin
3. **Créer un Realm**:
   - Cliquer sur le menu déroulant "master" en haut à gauche
   - Cliquer sur "Create Realm"
   - Nom: `alzheimer-realm`
   - Cliquer sur "Create"

4. **Créer un Client**:
   - Aller dans "Clients"
   - Cliquer sur "Create client"
   - Client ID: `alzheimer-angular-client`
   - Client type: `OpenID Connect`
   - Cliquer sur "Next"
   - Activer "Standard flow"
   - Cliquer sur "Save"
   - Dans les paramètres du client:
     - Valid redirect URIs: `http://localhost:4200/*`
     - Web origins: `http://localhost:4200`
     - Cliquer sur "Save"

5. **Créer des Utilisateurs**:
   - Aller dans "Users"
   - Cliquer sur "Add user"
   - Créer vos utilisateurs avec leurs rôles

6. **Créer des Rôles**:
   - Aller dans "Realm roles"
   - Créer les rôles: `ADMIN`, `SOIGNANT`, `MEDECIN`, `DOCTEUR`, `AIDANT`, `PATIENT`

---

## 🔄 Redémarrage

### Arrêter tous les services
```powershell
cd alzheimer-system-main/docker
docker-compose down
```

### Redémarrer tous les services
```powershell
cd alzheimer-system-main/docker
docker-compose up -d
```

**Votre realm et toutes vos configurations seront toujours là!** 🎉

---

## 📊 Vérifier la Persistance

Pour vérifier que les données sont bien persistées:

```powershell
# Voir le volume Docker
docker volume ls | findstr keycloak

# Inspecter le volume
docker volume inspect docker_keycloak-postgres-data
```

---

## ⚠️ Important

### Sauvegarde du Volume
Le volume `keycloak-postgres-data` contient toutes vos données Keycloak.

Pour sauvegarder:
```powershell
docker run --rm -v docker_keycloak-postgres-data:/data -v ${PWD}:/backup alpine tar czf /backup/keycloak-backup.tar.gz -C /data .
```

Pour restaurer:
```powershell
docker run --rm -v docker_keycloak-postgres-data:/data -v ${PWD}:/backup alpine tar xzf /backup/keycloak-backup.tar.gz -C /data
```

---

## 🎯 Prochaines Étapes

1. ✅ Keycloak avec persistance configuré
2. 🔄 Créer votre realm `alzheimer-realm`
3. 🔄 Créer vos utilisateurs et rôles
4. 🔄 Configurer le client Angular
5. 🔄 Tester l'authentification

---

**Date**: 2 mai 2026
**Statut**: ✅ KEYCLOAK AVEC PERSISTANCE POSTGRESQL OPÉRATIONNEL
**URL**: http://localhost:8081
