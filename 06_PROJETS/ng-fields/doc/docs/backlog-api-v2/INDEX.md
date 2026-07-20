# Backlog v2 — NG-Fields

**Période :** Juin–Juillet 2026
**Architecture :** 5 microservices (gateway:8080, auth:8081, client:8082, intervention:8083, media:8084)
**Contrainte :** Pas de Docker — exécution native uniquement

---

## Prérequis — Base de données

| # | Livrable | Référence |
|---|----------|-----------|
| — | Architecture globale (bases, rôles, connexion) | [docs/database/database-model.md](06_PROJETS/ng-fields/Doc/docs/database/database-model.md) |
| — | Base `ng_fields` (schémas, tables, DDL) | [docs/database/ng-fields-application.md](ng-fields-application.md) |
| — | Base `keycloak` (driver, vars d'env, démarrage) | [docs/database/keycloak.md](keycloak.md) |

## Sprint 0 — Setup environnement

| # | Livrable | Guide |
|---|----------|-------|
| 0.1 | PostgreSQL 18 + base `ng_fields` créée | [doc/Setup.md](Setup.md) |
| 0.2 | Keycloak 26.0.9 tourne sur `localhost:8088` | [01-setup-keycloak.md](01-setup-keycloak.md) |
| 0.3 | Realm `ng-fields` + clients OIDC + rôles importés | [02-configure-realm.md](02-configure-realm.md) |
| 0.4 | 5 microservices compilent et démarrent | [doc/Setup.md](Setup.md) |

## Sprint 1 — Auth (auth-service)

| # | Livrable | Guide |
|---|----------|-------|
| 1.1 | `GET /api/public/health` répond 200 | [03-spring-security.md](03-spring-security.md) |
| 1.2 | `POST /api/admin/users` crée user dans Keycloak + DB | [04-user-registration.md](04-user-registration.md) |
| 1.3 | `POST /api/public/register` inscription CLIENT_PORTAL | [04-user-registration.md](04-user-registration.md) |
| 1.4 | CRUD users (GET, PUT, DELETE) + assign rôle + status | [04-user-registration.md](04-user-registration.md) |
| 1.5 | `GET/PUT /api/users/me` profil utilisateur | [04-user-registration.md](04-user-registration.md) |

## Sprint 2 — Clients (client-service)

| # | Livrable | Guide |
|---|----------|-------|
| 2.1 | CRUD Clients (création, liste paginée, détail, modification, désactivation) | [05-client-crud.md](05-client-crud.md) |
| 2.2 | Recherche clients (`/search?q=`) | [05-client-crud.md](05-client-crud.md) |
| 2.3 | RBAC (ADMIN peut tout, MANAGER/TECHNICIAN lecture seule) | [05-client-crud.md](05-client-crud.md) |

## Sprint 3 — Interventions (intervention-service)

| # | Livrable | Guide |
|---|----------|-------|
| 3.1 | CRUD Interventions (création + 8 sections) | [06-intervention-crud.md](06-intervention-crud.md) |
| 3.2 | Pièces et consommables | [06-intervention-crud.md](06-intervention-crud.md) |

## Sprint 4 — Photos, Signatures, PDF

| # | Livrable | Guide |
|---|----------|-------|
| 4.1 | Upload photos avant/après | [07-photos-storage.md](07-photos-storage.md) |
| 4.2 | Signatures électroniques (3 zones) | [08-signatures.md](08-signatures.md) |
| 4.3 | Génération PDF | [09-pdf-generation.md](09-pdf-generation.md) |

## Sprint 5 — Hors-ligne, OpenProject, Dashboard

| # | Livrable | Guide |
|---|----------|-------|
| 5.1 | Sync offline | [10-hors-ligne.md](10-hors-ligne.md) |
| 5.2 | Intégration OpenProject | [11-openproject.md](11-openproject.md) |
| 5.3 | Dashboard stats | [12-dashboard.md](12-dashboard.md) |

---

## Arborescence des guides

```
doc/
├── backlog-api-v2/
│   ├── INDEX.md               ← Vous êtes ici
│   ├── database-model.md
│   └── guides/
│       ├── 01-setup-keycloak.md
│       ├── ...
│       └── 12-dashboard.md
├── database/
│   ├── database-model.md      ← Architecture globale (bases, rôles)
│   ├── ng-fields-application.md ← Schémas, tables, DDL
│   └── keycloak.md
├── architecture/
│   ├── stack-technique.md
│   └── flux-donnees.md
├── tests/
│   ├── postman-collection.json
│   └── postman-environment.json
└── Setup.md                    ← Démarrage rapide
```

## Comment lire

1. Suivre les guides dans l'ordre numérique
2. Chaque guide est autonome : prérequis → étapes → commandes → test
3. Tous les appels API passent par le gateway (`localhost:8080`)
