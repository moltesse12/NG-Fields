# Kit de Démarrage — NG-Fields

**Stack :** Spring Boot 4.1.0 / Java 25, Spring Cloud 2025.1.2, Keycloak 26.6.4, PostgreSQL 18

---

## Prérequis

| Outil | Version mini | Installation |
|-------|-------------|--------------|
| Java | 25 | [Java Download](https://jdk.java.net/25/) |
| Maven | Wrapper (inclus) | Via `Backend/<service>/mvnw` |
| PostgreSQL | 18 | [PostgreSQL](https://www.postgresql.org/download/) |
| Redis | 7+ | [Redis Windows](https://github.com/tporadowski/redis/releases) |
| Keycloak | 26.6.4 | [Keycloak](https://www.keycloak.org/downloads) |

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

Les schémas (`auth`, `client`, `intervention`) sont créés automatiquement par Hibernate `ddl-auto: update` au premier démarrage de chaque service.

---

## 3. Services requis

### Keycloak

```bash
# Télécharger et extraire Keycloak 26.6.4
cd keycloak-26.6.4/bin
./kc.bat start-dev --http-port=8088
```

Créer le realm `ng-fields` avec les clients OIDC et rôles (ADMIN, MANAGER, TECHNICIAN, CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER).

### Redis

```bash
# Démarrer Redis (Windows natif)
redis-server.exe
```

---

## 4. Backend (7 microservices + shared-lib)

Chaque service a son propre `mvnw.cmd` et son `.env`. Démarrer dans l'ordre :

```bash
cd Backend

# 1. Shared-lib (compiler en premier)
cd shared-lib; .\mvnw.cmd install -q

# 2. Gateway (port 8080)
cd gateway-service; .\mvnw.cmd spring-boot:run

# 3. Auth (port 8081)
cd auth-service; .\mvnw.cmd spring-boot:run

# 4. Client (port 8082)
cd client-service; .\mvnw.cmd spring-boot:run

# 5. Intervention (port 8083)
cd intervention-service; .\mvnw.cmd spring-boot:run

# 6. Media (port 8084)
cd media-service; .\mvnw.cmd spring-boot:run

# 7. Notification (port 8085)
cd notification-service; .\mvnw.cmd spring-boot:run

# 8. Report (port 8086)
cd report-service; .\mvnw.cmd spring-boot:run
```

Tous les appels API passent par le gateway sur `http://localhost:8080`.

---

## 5. Mobile (Flutter) — **Non démarré**

Le répertoire `mobile/` est vide. Flutter sera implémenté ultérieurement.

---

## 6. Structure du projet

```
ng-fields/
├── Backend/
│   ├── shared-lib/             → Bibliothèque partagée (exceptions, utils)
│   ├── gateway-service/        → Spring Cloud Gateway (WebFlux, port 8080)
│   ├── auth-service/           → Auth (Spring MVC, port 8081)
│   ├── client-service/         → Clients CRUD (Spring MVC, port 8082)
│   ├── intervention-service/   → Interventions (Spring MVC, port 8083)
│   ├── media-service/          → Fichiers (Spring MVC, port 8084)
│   ├── notification-service/   → Notifications email (Spring MVC, port 8085)
│   └── report-service/         → Rapports CSV/PDF (Spring MVC, port 8086)
├── Frontend/
│   ├── ng-web/                → Dashboard Angular 22+ (TS)
│   └── templates/             → Template Next.js
├── mobile/                    → App Flutter (Non démarré)
├── Doc/                       → Documentation
```

---

## 7. Commandes utiles

```bash
# Backend (chaque service)
cd Backend/<service> && .\mvnw.cmd compile          # Compiler
cd Backend/<service> && .\mvnw.cmd spring-boot:run  # Lancer

# Mobile (non démarré)
# cd mobile && flutter pub get
# cd mobile && flutter run

# Web
cd Frontend/ng-web && npm install               # Dépendances
cd Frontend/ng-web && npm start                 # Lancer → http://localhost:4200
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
http://localhost:8080/api/notifications/v3/api-docs
http://localhost:8080/api/reports/v3/api-docs
```

---

## 9. Postman

La collection de test se trouve dans `Backend/postman/` :

```bash
NG-Fields API.postman_collection.json    # 90 requêtes couvrant tous les US
NG-Fields Dev.postman_environment.json   # Variables d'environnement (base_url, kc_url, credentials)
```

Importer les deux fichiers dans Postman. Les tokens sont récupérés automatiquement par les requêtes Login.

---

_Version 5.0 — 23/07/2026 (Backend Complet)_
