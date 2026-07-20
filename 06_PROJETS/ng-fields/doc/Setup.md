# Kit de Démarrage — NG-Fields

**Stack :** Spring Boot 4.1.0 / Java 25, Spring Cloud 2025.1.2, Keycloak 26.0.9, PostgreSQL 18

---

## Prérequis

| Outil | Version mini | Installation |
|-------|-------------|--------------|
| Java | 25 | [Java Download](https://jdk.java.net/25/) |
| Maven | Wrapper (inclus) | Via `Backend/<service>/mvnw` |
| PostgreSQL | 18 | [PostgreSQL](https://www.postgresql.org/download/) |
| Redis | 7+ | [Redis Windows](https://github.com/tporadowski/redis/releases) |
| Keycloak | 26.0.9 | [Keycloak](https://www.keycloak.org/downloads) |

---

## 1. Cloner le dépôt

```bash
git clone https://github.com/moltesse12/ng-fields.git
cd ng-fields
```

---

## 2. Base de données

Créer la base `ng_fields` et l'utilisateur :

```sql
CREATE USER ng_fields_user WITH PASSWORD '${DB_PASSWORD}';
CREATE DATABASE ng_fields OWNER ng_fields_user;
```

Les schémas (`auth`, `client`, `intervention`) sont créés automatiquement par Flyway au premier démarrage de chaque service.

---

## 3. Services requis

### Keycloak

```bash
# Télécharger et extraire Keycloak 26.0.9
cd keycloak-26.0.9/bin
./kc.bat start-dev --http-port=8088
```

Créer le realm `ng-fields` avec les clients OIDC et rôles (ADMIN, MANAGER, TECHNICIAN, CLIENT_PORTAL).

### Redis

```bash
# Démarrer Redis (Windows natif)
redis-server.exe
```

---

## 4. Backend (5 microservices)

Chaque service a son propre `mvnw.cmd` et son `.env`. Démarrer dans l'ordre :

```bash
cd Backend

# 1. Gateway (port 8080) — point d'entrée unique
cd gateway-service; .\mvnw.cmd spring-boot:run

# 2. Auth (port 8081) — utilisateurs, rôles, Keycloak
cd auth-service; .\mvnw.cmd spring-boot:run

# 3. Client (port 8082) — gestion clients
cd client-service; .\mvnw.cmd spring-boot:run

# 4. Intervention (port 8083) — interventions, photos, signatures, PDF
cd intervention-service; .\mvnw.cmd spring-boot:run

# 5. Media (port 8084) — upload/download fichiers
cd media-service; .\mvnw.cmd spring-boot:run
```

Tous les appels API passent par le gateway sur `http://localhost:8080`.

---

## 5. Mobile (Flutter)

```bash
cd apps/mobile
flutter pub get
flutter run
```

---

## 6. Structure du projet

```
ng-fields/
├── Backend/
│   ├── gateway-service/        → Spring Cloud Gateway (WebFlux, port 8080)
│   ├── auth-service/           → Auth (Spring MVC, port 8081)
│   ├── client-service/         → Clients CRUD (Spring MVC, port 8082)
│   ├── intervention-service/   → Interventions (Spring MVC, port 8083)
│   └── media-service/          → Fichiers (Spring MVC, port 8084)
├── apps/
│   ├── mobile/                 → App Flutter (Dart)
│   └── web/                    → Dashboard Angular (TS)
├── infra/
│   ├── docker-compose.yml
│   ├── supabase/
│   │   ├── schema.sql
│   │   └── seed.sql
│   └── keycloak/
│       └── realm-export.json
├── doc/
│   ├── Setup.md                ← vous êtes ici
│   ├── Technologies.md
│   ├── docs/
│   │   ├── backlog-api-v2/     → Guides API
│   │   ├── database/           → Modèle de données
│   │   └── tests/              → Postman
│   └── ...
└── .github/workflows/
```

---

## 7. Commandes utiles

```bash
# Backend (chaque service)
cd Backend/<service> && .\mvnw.cmd compile          # Compiler
cd Backend/<service> && .\mvnw.cmd spring-boot:run  # Lancer

# Mobile
cd apps/mobile && flutter pub get                   # Dépendances
cd apps/mobile && flutter run                       # Lancer

# Web
cd apps/web && npm install                          # Dépendances
cd apps/web && ng serve                             # Lancer
```

---

## 8. Documentation API

```bash
# Swagger UI (via gateway)
http://localhost:8080/swagger-ui.html

# Spécifications OpenAPI par service
http://localhost:8080/api/clients/v3/api-docs
http://localhost:8080/api/interventions/v3/api-docs
http://localhost:8080/api/media/v3/api-docs
```

---

## 9. Postman

La collection de test se trouve dans `doc/docs/tests/` :

```bash
postman-collection.json    # 32 requêtes couvrant auth, clients, media
postman-environment.json   # Variables d'environnement (base_url, kc_url, credentials)
```

Importer les deux fichiers dans Postman. Les tokens sont récupérés automatiquement par les requêtes Login.

---

_Version 3.0 — 03/07/2026_
