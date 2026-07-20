# Tests API NG-Fields (Postman)

## Prérequis

- **Keycloak** sur `http://localhost:8088` (realm `ng-fields`)
- **API Gateway** sur `http://localhost:8080` (route vers auth/client/intervention/media)

## Installation

1. `File` → `Import` → onglet `Raw text`
2. Ouvrir `docs/tests/postman-collection.json`, copier le contenu
3. Coller dans Postman → `Import`
4. Sélectionner l'environnement `NG-Fields API - Dev`

## Requêtes

### Health & Auth (1-6)

| # | Requête | Username | Mot de passe | Code |
|---|---------|----------|-------------|:---:|
| 1 | Health check | — | — | 200 |
| 2 | Login CLIENT_PORTAL | `client1` | `Client123!` | 200 |
| 3 | Login TECHNICIEN | `tech1` | `Tech123!` | 200 |
| 4 | Login MANAGER | `manager1` | `Mngr123!` | 200 |
| 5 | Login ADMIN | `admin` | `Admin123!` | 200 |
| 6 | Identifiants invalides | `user_inexistant` | `mauvais_mdp` | 401 |

### Inscription (7-8)

| # | Requête | Body | Code |
|---|---------|------|:---:|
| 7 | Inscription publique | `inscrit_test` / `InscritPass123!` | 201 |
| 8 | Inscription doublon | Même username | 400 |

### Profil (9-11)

| # | Requête | Token | Code |
|---|---------|-------|:---:|
| 9 | GET /users/me (CLIENT_PORTAL) | `userToken` | 200 |
| 10 | PUT /users/me (CLIENT_PORTAL) | `userToken` | 200 |
| 11 | GET /users/me (non auth) | Aucun | 401 |

### Admin Users (12-19)

| # | Requête | Auth | Code |
|---|---------|------|:---:|
| 12 | Create user | `adminToken` | 201 |
| 13 | List users | `adminToken` | 200 |
| 14 | Get user by ID | `adminToken` | 200 |
| 15 | Update user | `adminToken` | 200 |
| 16 | Assign role | `adminToken` | 200 |
| 17 | Update status (désactiver) | `adminToken` | 200 |
| 18 | Reset password | `adminToken` | 200 |
| 19 | Delete user | `adminToken` | 204 |

### Clients (20-26)

| # | Requête | Auth | Code |
|---|---------|------|:---:|
| 20 | Créer un client (ADMIN) | `adminToken` | 201 |
| 21 | Lister les clients | `adminToken` | 200 |
| 22 | Rechercher un client | `adminToken` | 200 |
| 23 | Détail d'un client | `adminToken` | 200 |
| 24 | Modifier un client (ADMIN) | `adminToken` | 200 |
| 25 | Supprimer un client (ADMIN) | `adminToken` | 204 |
| 26 | Créer un client (CLIENT_PORTAL — 403) | `userToken` | 403 |

### Media (27-30)

| # | Requête | Auth | Code |
|---|---------|------|:---:|
| 27 | Upload file | Aucun | 200 |
| 28 | Upload base64 | Aucun | 200 |
| 29 | Download file | Aucun | 200 |
| 30 | Delete file | Aucun | 204 |

### Access Control (31-32)

| # | Requête | Auth | Code |
|---|---------|------|:---:|
| 31 | Lister clients (TECHNICIEN — OK) | `technicianToken` | 200 |
| 32 | Créer client (TECHNICIEN — 403) | `technicianToken` | 403 |

### Interventions (33-48)

| # | Requête | Auth | Code |
|---|---------|------|:---:|
| 33 | Créer une intervention (TECHNICIEN) | `technicianToken` | 201 |
| 34 | Lister les interventions (ADMIN) | `adminToken` | 200 |
| 35 | Détail d'une intervention | `adminToken` | 200 |
| 36 | Modifier une intervention | `adminToken` | 200 |
| 37 | Planifier (schedule) | `technicianToken` | 200 |
| 38 | Ajouter équipement | `technicianToken` | 200 |
| 39 | Ajouter diagnostic | `technicianToken` | 200 |
| 40 | Ajouter résultat | `technicianToken` | 200 |
| 41 | Ajouter recommandations | `technicianToken` | 200 |
| 42 | Ajouter item | `technicianToken` | 201 |
| 43 | Modifier item | `technicianToken` | 200 |
| 44 | Démarrer l'intervention | `technicianToken` | 200 |
| 45 | Fermer l'intervention | `technicianToken` | 200 |
| 46 | Télécharger PDF | `technicianToken` | 200 |
| 47 | Créer intervention (CLIENT_PORTAL — 403) | `userToken` | 403 |
| 48 | Interventions par client | `adminToken` | 200 |

### Interventions Lifecycle & Sync (49-52)

| # | Requête | Auth | Code |
|---|---------|------|:---:|
| 49 | Sync client data (ADMIN) | `adminToken` | 200 |
| 50 | Sync interventions | `technicianToken` | 200 |
| 51 | Annuler intervention | `adminToken` | 200 |
| 52 | Assigner intervention | `adminToken` | 200 |

## Ordre d'exécution

Exécuter dans l'ordre : **1 → 2 → 3 → 4 → 5 → 6 → 7 → 8 → 9 → 10 → 11 → 12 → 13 → 14 → 15 → 16 → 17 → 18 → 19 → 20 → 21 → 22 → 23 → 24 → 25 → 26 → 27 → 28 → 29 → 30 → 31 → 32 → 33 → 34 → 35 → ... → 52**

Les requêtes dépendent de variables auto-générées :
- Tokens (2-5) → requis pour toutes les requêtes authentifiées
- `testClientId` (20) → requis pour les tests client (21-25)
- `testInterventionId` (33) → requis pour les tests intervention (34-52)

## Variables

| Variable | Valeur | Remplie par |
|----------|--------|-------------|
| `base_url` | `http://localhost:8080` | Manuel (gateway) |
| `kc_url` | `http://localhost:8088` | Manuel (Keycloak) |
| `client_secret` | `c5c0f83e-...` | Manuel |
| `adminToken` | *(auto)* | Requête 5 |
| `managerToken` | *(auto)* | Requête 4 |
| `technicianToken` | *(auto)* | Requête 3 |
| `userToken` | *(auto)* | Requête 2 |
| `testClientId` | *(auto)* | Requête 20 |
| `testInterventionId` | *(auto)* | Requête 33 |
