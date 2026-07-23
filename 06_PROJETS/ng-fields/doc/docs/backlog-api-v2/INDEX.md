# Backlog v2 — NG-Fields

**Période :** Juin–Juillet 2026
**Architecture :** 7 microservices + shared-lib (gateway:8080, auth:8081, client:8082, intervention:8083, media:8084, notification:8085, report:8086)
**Contrainte :** Pas de Docker — exécution native uniquement
**Mis à jour :** 23/07/2026 (Backend Complet — 65 tests, 90 endpoints Postman)

---

## Prérequis — Base de données

| # | Livrable | Référence |
|---|----------|-----------|
| — | Architecture globale (bases, rôles, connexion) | `docs/database/database-model.md` |
| — | Base `ng_fields` (schémas, tables, DDL) | [docs/database/ng-fields-application.md](ng-fields-application.md) |
| — | Base `keycloak` (driver, vars d'env, démarrage) | [docs/database/keycloak.md](keycloak.md) |

## Sprint 0 — Setup environnement

| # | Livrable | Guide | Statut |
|---|----------|-------|--------|
| 0.1 | PostgreSQL 18 + base `ng_fields` créée | [doc/Setup.md](Setup.md) | 🟢 COMPLETED |
| 0.2 | Keycloak 26.6.4 tourne sur `localhost:8088` | [01-setup-keycloak.md](01-setup-keycloak.md) | 🟢 COMPLETED |
| 0.3 | Realm `ng-fields` + clients OIDC + rôles importés | [02-configure-realm.md](02-configure-realm.md) | 🟢 COMPLETED |
| 0.4 | 7 microservices compilent et démarrent | [doc/Setup.md](Setup.md) | 🟢 COMPLETED |

## Sprint 1 — Auth (auth-service)

| # | Livrable | Guide | Statut |
|---|----------|-------|--------|
| 1.1 | `GET /api/public/health` répond 200 | [03-spring-security.md](03-spring-security.md) | 🟢 COMPLETED |
| 1.2 | `POST /api/admin/users` crée user dans Keycloak + DB | [04-user-registration.md](04-user-registration.md) | 🟢 COMPLETED |
| 1.3 | `POST /api/public/register` inscription CLIENT_USER | [04-user-registration.md](04-user-registration.md) | 🟢 COMPLETED |
| 1.4 | CRUD users (GET, PUT, DELETE) + assign rôle + status | [04-user-registration.md](04-user-registration.md) | 🟢 COMPLETED |
| 1.5 | `GET/PUT /api/users/me` profil utilisateur | [04-user-registration.md](04-user-registration.md) | 🟢 COMPLETED |
| 1.6 | Gestion entreprises (Company CRUD) | [04-user-registration.md](04-user-registration.md) | 🟢 COMPLETED |
| 1.7 | Utilisateurs entreprise (CLIENT_ADMIN CRUD) | [04-user-registration.md](04-user-registration.md) | 🟢 COMPLETED |

## Sprint 2 — Clients (client-service)

| # | Livrable | Guide | Statut |
|---|----------|-------|--------|
| 2.1 | CRUD Clients (création, liste paginée, détail, modification, désactivation) | [05-client-crud.md](05-client-crud.md) | 🟢 COMPLETED |
| 2.2 | Recherche clients (`/search?q=`) | [05-client-crud.md](05-client-crud.md) | 🟢 COMPLETED |
| 2.3 | RBAC (ADMIN peut tout, MANAGER/TECHNICIAN lecture seule) | [05-client-crud.md](05-client-crud.md) | 🟢 COMPLETED |

## Sprint 3 — Interventions (intervention-service)

| # | Livrable | Guide | Statut |
|---|----------|-------|--------|
| 3.1 | CRUD Interventions (création + 8 sections) | [06-intervention-crud.md](06-intervention-crud.md) | 🟢 COMPLETED |
| 3.2 | Pièces et consommables | [06-intervention-crud.md](06-intervention-crud.md) | 🟢 COMPLETED |
| 3.3 | Batch sync (offline→online) | intervention-service | 🟢 COMPLETED |
| 3.4 | Dashboard Manager (KPIs, stats, export) | intervention-service | 🟢 COMPLETED |
| 3.5 | Planning Techniciens (weekly schedule, assign) | intervention-service | 🟢 COMPLETED |
| 3.6 | SSE Real-time Events | intervention-service | 🟢 COMPLETED |
| 3.7 | Client Dashboard (KPIs filtered by company) | intervention-service | 🟢 COMPLETED |

## Sprint 4 — Photos, Signatures, PDF, Push

| # | Livrable | Référence | Statut |
|---|----------|-----------|--------|
| 4.1 | Upload photos avant/après | intervention-service | 🟢 COMPLETED |
| 4.2 | Signatures électroniques (3 zones) | intervention-service | 🟢 COMPLETED |
| 4.3 | Génération PDF | intervention-service | 🟢 COMPLETED |
| 4.4 | Push notifications Firebase (conditional) | notification-service | 🟢 COMPLETED |
| 4.5 | Email Resend API | auth-service + intervention-service | 🟢 COMPLETED |

## Sprint 5 — Hors-ligne, Dashboard, Tests

| # | Livrable | Référence | Statut |
|---|----------|-----------|--------|
| 5.1 | Sync offline (batch) | intervention-service | 🟢 COMPLETED |
| 5.2 | Dashboard stats | intervention-service | 🟢 COMPLETED |
| 5.3 | Actuator + Métriques + Logs JSON | 4 services | 🟢 COMPLETED |
| 5.4 | Swagger @Tag/@Operation/@ApiResponse | tous controllers | 🟢 COMPLETED |
| 5.5 | Clean Architecture (InterventionListDTO, ClientListDTO) | intervention-service | 🟢 COMPLETED |
| 5.6 | Templates PDF/Email (CRUD) | report-service | 🟢 COMPLETED |
| 5.7 | 65 unit tests | 3 services | 🟢 COMPLETED |

---

## Arborescence des guides

```
doc/
├── backlog-api-v2/
│   ├── INDEX.md               ← Vous êtes ici
│   └── guides/
│       ├── 01-setup-keycloak.md
│       ├── ...
│       └── 06-intervention-crud.md
├── database/
│   ├── database-model.md      ← Architecture globale (bases, rôles)
│   ├── ng-fields-application.md ← Schémas, tables, DDL
│   └── keycloak.md
├── architecture/
│   ├── stack-technique.md
│   └── flux-donnees.md
├── tests/
│   └── postman-collection.md
└── ../Setup.md                ← Démarrage rapide
```

## Comment lire

1. Suivre les guides dans l'ordre numérique
2. Chaque guide est autonome : prérequis → étapes → commandes → test
3. Tous les appels API passent par le gateway (`localhost:8080`)
