# ~~OpenProject - Configuration NG-Fields~~ ❌ **SUPPRIMÉ**

> **Décision cadrage 21/07/2026 :** L'intégration OpenProject est supprimée du périmètre.
> Raison : La gestion des tickets est gérée directement en interne. Pas de synchronisation externe nécessaire.
> Ce document est conservé à titre historique uniquement.

## 🎯 Objectif
> Guide de configuration d'OpenProject pour gérer le développement du projet NG-Fields

---

## 1. Création du Projet

### 1.1 Informations générales

| Champ | Valeur |
|-------|--------|
| **Nom du projet** | NG-Fields |
| **Description** | Application mobile de gestion des interventions terrain pour NG-STARs |
| **Visibilité** | Privé (équipe interne) |
| **Statut** | En cours |
| **Parent** | NG-STARs IT (optionnel) |

### 1.2 URL suggérée
```
https://openproject.ng-stars.com/projects/ng-fields
```

---

## 2. Configuration du Projet

### 2.1 Modules à activer

| Module | Activation | Usage |
|--------|------------|-------|
| Work packages | ✅ Oui | Tâches et user stories |
| Gantt | ✅ Oui | Planning visuel |
| Calendar | ✅ Oui | Échéances |
| News | ✅ Oui | Communications |
| Documents | ✅ Oui | Documentation |
| Wiki | ✅ Oui | Guides et process |
| Repository | ✅ Oui | Liaison Git (optionnel) |
| Time tracking | ✅ Oui | Suivi temps |
| Budget | ❌ Non | Pas de budget dans OpenProject |
| Meetings | ✅ Oui | Réunions sprint |

### 2.2 Type de projet

**Choix recommandé :** `Scrum` (méthodologie agile)

---

## 3. Structure du Backlog

### 3.1 Épopées (Epics)

Créer 4 épopées principales :

| # | Épopée | Description |
|---|--------|-------------|
| E01 | Authentification & Profil | Login, logout, gestion profil |
| E02 | Formulaire Intervention | Saisie complète FI-01-2025 |
| E03 | Mode Offline & Sync | Fonctionnement hors-ligne |
| E04 | Dashboard & Reporting | Vue manager et stats |

### 3.2 User Stories (sous chaque Épopée)

#### E01 - Authentification & Profil

| US | Description | Points | Sprint |
|----|-------------|--------|--------|
| US-101 | En tant que technicien, je veux me connecter avec email/mot de passe | 5 | S2 |
| US-102 | En tant que technicien, je veux me déconnecter | 2 | S2 |
| US-103 | En tant que technicien, je veux modifier mon mot de passe | 3 | S2 |
| US-104 | En tant que technicien, je veux récupérer mon mot de passe | 5 | S2 |

#### E02 - Formulaire Intervention

| US | Description | Points | Sprint |
|----|-------------|--------|--------|
| US-201 | En tant que technicien, je veux saisir les infos client | 5 | S3 |
| US-202 | En tant que technicien, je veux saisir les horaires | 3 | S3 |
| US-203 | En tant que technicien, je veux saisir le diagnostic | 5 | S3 |
| US-204 | En tant que technicien, je veux signer numériquement | 8 | S4 |
| US-205 | En tant que client, je veux signer l'intervention | 5 | S4 |

#### E03 - Mode Offline & Sync

| US | Description | Points | Sprint |
|----|-------------|--------|--------|
| US-301 | En tant que technicien, je veux saisir en offline | 13 | S7 |
| US-302 | En tant que technicien, je veux voir le statut sync | 3 | S7 |
| US-303 | En tant que système, je veux sync automatiquement | 8 | S8 |

#### E04 - Dashboard & Reporting

| US | Description | Points | Sprint |
|----|-------------|--------|--------|
| US-401 | En tant que manager, je veux voir toutes les interventions | 8 | S9 |
| US-402 | En tant que manager, je veux filtrer les interventions | 5 | S9 |
| US-403 | En tant que manager, je veux exporter en CSV | 3 | S9 |

---

## 4. Sprints

### 4.1 Configuration Scrum

| Paramètre | Valeur |
|-----------|--------|
| Durée sprint | **2 semaines** |
| Jour de début | Lundi |
| Jour de rétrospective | Vendredi (fin de sprint) |
| Capacité équipe | 2 devs full-time |

### 4.2 Sprints Planifiés

| Sprint | Dates | Objectif | Points |
|--------|-------|----------|--------|
| S1 | 15-21/04/26 | Setup & Validation CdC | - |
| S2 | 22-30/04/26 | Auth & Fondation | 15 |
| S3 | 01-07/05/26 | Formulaire Core | 25 |
| S4 | 08-15/05/26 | Signature & Clients | 30 |
| S5 | 16-23/05/26 | Photos & Notifications | 25 |
| S6 | 24-31/05/26 | PDF & Envoi | 20 |
| S7 | 01-07/06/26 | Mode Offline | 25 |
| S8 | 08-15/06/26 | Sync & Stabilisation | 20 |
| S9 | 16-23/06/26 | Dashboard Manager | 20 |
| S10 | 24-30/06/26 | UAT & Lancement | 15 |

**Total estimé :** ~195 story points

---

## 5. Work Packages Types

### 5.1 Types de tickets

| Type | Icône | Usage |
|------|-------|-------|
| Épopée | 🏔️ | Groupe de user stories |
| User Story | 📖 | Fonctionnalité (format INVEST) |
| Tâche | ✅ | Sous-tâche technique |
| Bug | 🐛 | Correction |
| Feature | ✨ | Amélioration |

### 5.2 Cycle de vie des tickets

```
Nouvelle → En cours → En review → Terminée
   ↓           ↓           ↓           ↓
             Bloquée    Bloquée    
```

---

## 6. Champs Personnalisés

### 6.1 Champs recommandés

| Champ | Type | Visible dans |
|-------|------|--------------|
| Sprint | Liste (S1-S10) | Tous |
| Priorité métier | Liste (P0-P3) | User Stories |
| Effort | Liste (XS, S, M, L, XL) | Tous |
| Plateforme | Multi-check (iOS, Android, Web) | Tous |

### 6.2 Valeurs par défaut

| Champ | Valeur |
|-------|--------|
| Priorité | Normale |
| Sprint | Non assigné |
| Assigné à | Non assigné |

---

## 7. Vue Kanban (Optionnel)

### 7.1 Configuration

| Colonne | Couleur | Tickets |
|---------|---------|---------|
| À faire | Gris | Nouveaux tickets |
| En cours | Bleu | Sprint en cours |
| En review | Orange | PR/Review |
| Terminé | Vert | Done |

### 7.2 Filtres recommandés

- [ ] Projet = NG-Fields
- [ ] Sprint = Courant
- [ ] Type ≠ Épopée

---

## 8. Intégration Git

### 8.1 Configuration Repository

**Type :** GitHub / GitLab

| Setting | Valeur |
|---------|--------|
| URL | https://github.com/ng-stars/ng-fields |
| Branch par défaut | main |
| Branches sprints | feature/S*, fix/S* |

### 8.2 Conventions de commits

```
[US-XXX] Description courte

Format:
feat(US-101): ajout connexion utilisateur
fix(US-205): correction signature client
docs: mise à jour README
```

### 8.3 Work packages links

Les commits doivent référencer les US :
```
git commit -m "feat(US-201): ajout saisie client

Closes #OP/US-201"
```

---

## 9. Notifications & Alertes

### 9.1 Abonnements par défaut

| Événement | Notification |
|----------|---------------|
| Ticket assigné | Email + In-app |
| Sprint démarré | In-app |
| Sprint terminé | In-app |
| Nouveau commentaire | Email (optionnel) |
| Date d'échéance proche | In-app (1 jour avant) |

### 9.2 Réunions récurrentes

| Réunion | Fréquence | Durée | Participants |
|---------|-----------|-------|--------------|
| Daily standup | Quotidien | 15 min | Équipe dev |
| Sprint planning | Début sprint | 2h | Équipe + PO |
| Rétrospective | Fin sprint | 1h30 | Équipe dev |
| Revue sprint | Fin sprint | 1h | Équipe + Stakeholders |

---

## 10. Rapports & KPIs

### 10.1 Dashboards recommandés

| Rapport | Fréquence | Audience |
|---------|-----------|----------|
| Burndown chart | Quotidien | Équipe dev |
| Sprint velocity | Fin sprint | Équipe + PO |
| Tâches par statut | Hebdomadaire | PO |
| Temps passé | Hebdomadaire | Manager |

### 10.2 Indicateurs de suivi

| KPI | Calcul | Cible |
|-----|--------|-------|
| Velocity | Points terminés / sprint | 25 points |
| Taux completion | Terminé / Total US | 80% fin sprint |
| WIP | En cours max | 6 tickets |

---

## 11. Checklist de Setup

### Phase 1 : Création projet
- [ ] Créer projet "NG-Fields"
- [ ] Activer modules (Work packages, Wiki, etc.)
- [ ] Configurer types de tickets
- [ ] Créer champs personnalisés

### Phase 2 : Backlog
- [ ] Créer les 4 Épopées
- [ ] Créer toutes les User Stories
- [ ] Ajouter points de story
- [ ] Définir sprints

### Phase 3 : Équipe
- [ ] Inviter membres (5 personnes)
- [ ] Configurer rôles
- [ ] Former l'équipe (15 min)

### Phase 4 : Intégration
- [ ] Lier repository Git
- [ ] Configurer webhooks
- [ ] Tester commit links

---

## 12. Liens Utiles

| Ressource | URL |
|-----------|-----|
| Documentation OpenProject | https://www.openproject.org/docs/ |
| Guide Scrum | https://www.openproject.org/docs/user-guide/projects/ |
| API OpenProject | https://www.openproject.org/docs/api/ |

---

## 13. Contacts Support

| Rôle | Nom | Email |
|------|-----|-------|
| Admin OpenProject | David | david@ng-stars.com |
| Chef de projet | Barnabe | barnabe@ng-stars.com |
| Super Admin | Gildard | gildard@ng-stars.com |

---

**Dernière mise à jour :** 15/04/2026  
**Version :** 1.0
