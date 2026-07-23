# Testing

**Mis à jour :** 23/07/2026 (Backend Complet)

## Tests unitaires — 72 tests (tous passent)

### intervention-service (49 tests)

| Fichier | Tests | Couverture |
|---------|-------|-----------|
| `InterventionServiceTest` | 25 | CRUD, getStats, batch sync, GPS update, SSE push |
| `InterventionStatusServiceTest` | 16 | State machine (toutes transitions), erreurs |
| `ExportServiceTest` | 8 | CSV/HTML escaping (injection, Unicode, quotes) |

### auth-service (21 tests)

| Fichier | Tests | Couverture |
|---------|-------|-----------|
| `UserServiceTest` | 11 | CRUD, roles Keycloak, changePassword (vérification old mdp), updateRole |
| `CompanyServiceTest` | 10 | CRUD, addCompanyUser, soft delete, multi-tenant |

### notification-service (3 tests)

| Fichier | Tests | Couverture |
|---------|-------|-----------|
| `PushServiceNoopTest` | 3 | sendPush (noop), registerToken (noop), fallback Firebase |

### report-service (7 tests)

| Fichier | Tests | Couverture |
|---------|-------|-----------|
| `TemplateRenderingTest` | 7 | Email templates rendering (5 templates), PDF template config validation (2 tests) |

## Commandes

```bash
# Tous les services
cd Backend && mvn test

# Un seul service
cd Backend/intervention-service && mvn test

# Un seul test
cd Backend/intervention-service && mvn test -Dtest=InterventionServiceTest

# Avec couverture JaCoCo
cd Backend && mvn clean verify
```

## Running Build

```bash
# Tous les services
cd Backend && mvn clean install

# Un seul service (avec dépendances)
cd Backend && mvn clean install -pl intervention-service -am
```

## Collection Postman

La collection de test fonctionnel se trouve dans `Backend/postman/` :

- `NG-Fields API.postman_collection.json` — **91 requêtes** couvrant tous les US
- `NG-Fields Dev.postman_environment.json` — Variables d'environnement

Les tokens sont récupérés automatiquement par les requêtes Login.

## Vérification fonctionnelle

- **Collection Postman** — 91 endpoints testés
- **Swagger UI** — `http://localhost:8080/swagger-ui.html`
- **Tests manuels** via Swagger

## Planifié

- Tests d'intégration avec Testcontainers (PostgreSQL en mémoire)
- Tests de sécurité (rôles, permissions)
- Tests bout-en-bout via le gateway
- Tests charge k6 (P95 < 2s)
