# 📄 Toutes les Pages HTML de votre Projet

## 🏠 Page Principale
- **index.html**: http://localhost:4200

---

## 👨‍⚕️ Pages Médecin (Doctor)

| Page HTML | URL | Description |
|-----------|-----|-------------|
| `doctor-dashboard-simple.component.html` | http://localhost:4200/doctor-dashboard | Dashboard principal médecin |
| `doctor-patients.component.html` | http://localhost:4200/doctor-patients | Liste des patients |
| `doctor-appointments.component.html` | http://localhost:4200/doctor-appointments | Rendez-vous |
| `medical-agenda.component.html` | http://localhost:4200/medical-agenda | Agenda médical |
| `doctor-reports.component.html` | http://localhost:4200/doctor-reports | Rapports médicaux |
| `doctor-report-create.component.html` | http://localhost:4200/doctor-report-create | Créer un rapport |
| `doctor-settings.component.html` | http://localhost:4200/doctor-settings | Paramètres |

---

## 👩‍⚕️ Pages Soignant

| Page HTML | URL | Description |
|-----------|-----|-------------|
| `soignant-dashboard.component.html` | http://localhost:4200/soignant-dashboard | Dashboard soignant |
| `soignant-patients-page.component.html` | http://localhost:4200/soignant-patients | Mes patients |
| `soignant-rapports-page.component.html` | http://localhost:4200/soignant-rapports | Rapports médicaux |
| `soignant-agenda-page.component.html` | http://localhost:4200/soignant-agenda | Agenda visuel |
| `soignant-rapports-hebdo-page.component.html` | http://localhost:4200/soignant-rapports-hebdo | Rapports hebdomadaires |
| `soignant-fiches-page.component.html` | http://localhost:4200/soignant-fiches | Fiches de transmission |
| `fiche-transmission-hebdo.component.html` | http://localhost:4200/soignant-fiche-transmission/:patientId | Fiche transmission (avec ID patient) |
| `soignant-notifications-page.component.html` | http://localhost:4200/soignant-notifications | Notifications |

---

## 👨‍👩‍👧 Pages Aidant

| Page HTML | URL | Description |
|-----------|-----|-------------|
| `aidant-dashboard.component.html` | http://localhost:4200/aidant-dashboard | Dashboard aidant |
| `aidant-patients.component.html` | http://localhost:4200/aidant-patients | Mes patients |
| `aidant-planning.component.html` | http://localhost:4200/aidant-planning | Planning |
| `aidant-rapports.component.html` | http://localhost:4200/aidant-rapports | Rapports |

---

## 🔧 Pages Admin

| Page HTML | URL | Description |
|-----------|-----|-------------|
| `manage-users.component.html` | http://localhost:4200/admin/manage-users | Gestion des utilisateurs |
| `user-type-list.component.html` | http://localhost:4200/admin/user-types | Types d'utilisateurs |
| `patient-list.component.html` | http://localhost:4200/admin/patients | Liste des patients |
| `patient-dashboard.component.html` | http://localhost:4200/admin/patient-dashboard | Dashboard patient |

---

## 🧩 Composants Partagés (Shared)

Ces composants sont utilisés dans d'autres pages:

| Composant HTML | Utilisé dans |
|----------------|--------------|
| `app-shell.component.html` | Layout admin |
| `sidebar.component.html` | Tous les dashboards |
| `topbar.component.html` | Layout admin |
| `notification-bell.component.html` | Tous les dashboards |
| `agenda-daily-view.component.html` | Agenda soignant |
| `agenda-weekly-view.component.html` | Agenda soignant |
| `agenda-event-card.component.html` | Agenda soignant |
| `fiche-transmission-panel.component.html` | Fiches soignant |
| `user-type-dialog.component.html` | Dialogue types utilisateurs |

---

## 🎯 URLs Principales à Tester

### Pour commencer:
1. **Page d'accueil**: http://localhost:4200
2. **Dashboard Médecin**: http://localhost:4200/doctor-dashboard
3. **Dashboard Soignant**: http://localhost:4200/soignant-dashboard
4. **Dashboard Aidant**: http://localhost:4200/aidant-dashboard
5. **Admin - Gestion utilisateurs**: http://localhost:4200/admin/manage-users

---

## 📝 Note

Toutes ces pages sont accessibles car les guards d'authentification sont désactivés (mode bypass). Vous pouvez naviguer librement entre toutes les pages.

**Pour voir une page spécifique, utilisez l'URL correspondante dans votre navigateur.**
