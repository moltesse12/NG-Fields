# Architecture PostgreSQL — NG-Fields

**Révision :** 2.0 — Juillet 2026
**Moteur :** PostgreSQL 18 (localhost:5432)

---

## 1. Bases de données

```
┌──────────────────────────────────────────────────────────────┐
│                  PostgreSQL 18 (localhost:5432)                │
├──────────────────┬───────────────────────────────────────────┤
│     keycloak     │                 ng_fields                  │
│  (keycloak_user) │            (ng_fields_user)                │
├──────────────────┼───────────────────────────────────────────┤
│ Base dédiée à    │ Base principale : 3 schémas               │
│ Keycloak IAM     │  - auth : utilisateurs, audit_logs        │
│ (~80 tables)     │  - client : clients                       │
│                  │  - intervention : interventions, photos    │
│                  │    signatures, pièces, consommables       │
└──────────────────┴───────────────────────────────────────────┘
```

Chaque microservice utilise son propre schéma :

| Service | Schéma | Tables |
|---------|--------|--------|
| auth-service | `auth` | `users`, `audit_logs` |
| client-service | `client` | `clients` |
| intervention-service | `intervention` | `interventions`, `photos`, `signatures`, `pieces`, `consommables` |

---

## 2. Rôles applicatifs

| Rôle | Base | Connection Limit | Mot de passe | Usage |
|------|------|-----------------|-------------|-------|
| `ng_fields_user` | `ng_fields` | 50 | `${DB_PASSWORD}` | API Spring Boot (tous services) |
| `keycloak_user` | `keycloak` | 20 | `${KC_DB_PASSWORD}` | Keycloak IAM |

> **Règle :** Jamais le superuser `postgres` dans les applications.

---

## 3. Paramètres de connexion

| Composant | JDBC URL | Base | User |
|-----------|----------|------|------|
| Auth-service | `jdbc:postgresql://localhost:5432/ng_fields?currentSchema=auth` | `ng_fields` | `ng_fields_user` |
| Client-service | `jdbc:postgresql://localhost:5432/ng_fields?currentSchema=client` | `ng_fields` | `ng_fields_user` |
| Intervention-service | `jdbc:postgresql://localhost:5432/ng_fields?currentSchema=intervention` | `ng_fields` | `ng_fields_user` |
| Media-service | Pas de DB (filesystem) | — | — |
| Keycloak | `jdbc:postgresql://localhost:5432/keycloak` | `keycloak` | `keycloak_user` |

---

## 4. Documentation par base

| Base | Document | Contenu |
|------|----------|---------|
| `ng_fields` | [ng-fields-application.md](ng-fields-application.md) | 3 schémas, DDL, mapping JPA |
| `keycloak` | [keycloak.md](keycloak.md) | Driver, vars d'env, démarrage, notes Liquibase |
