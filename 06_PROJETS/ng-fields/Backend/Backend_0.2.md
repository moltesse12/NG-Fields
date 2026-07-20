# Backend_0.2 — Documentation Complète du Backend NG-Fields

> **Version**: 0.2  
> **Date**: Juillet 2026  
> **Auteur**: NG-STARs  
> **Statut**: En cours de développement

---

## Table des matières

1. [Vue d'ensemble](#1-vue-densemble)
2. [Stack technique](#2-stack-technique)
3. [Architecture microservices](#3-architecture-microservices)
4. [Détail des services](#4-détail-des-services)
   - 4.1 [shared-lib](#41-shared-lib)
   - 4.2 [gateway-service](#42-gateway-service)
   - 4.3 [auth-service](#43-auth-service)
   - 4.4 [client-service](#44-client-service)
   - 4.5 [intervention-service](#45-intervention-service)
   - 4.6 [media-service](#46-media-service)
   - 4.7 [notification-service](#47-notification-service)
   - 4.8 [report-service](#48-report-service)
5. [Base de données](#5-base-de-données)
6. [Sécurité](#6-sécurité)
7. [Infrastructure & Déploiement](#7-infrastructure--déploiement)
8. [Intégrations externes](#8-intégrations-externes)
9. [Tests](#9-tests)
10. [Décisions techniques](#10-décisions-techniques)
11. [Roadmap & Prochaines étapes](#11-roadmap--prochaines-étapes)
12. [Annexes](#12-annexes)

---

## 1. Vue d'ensemble

NG-Fields est une **Solution de Digitalisation et Centralisation de la Gestion des Interventions terrain** développée par NG-STARs (Togo). Le backend est une architecture **microservices** construite avec **Spring Boot 4.1.0** et **Java 25**, sécurisée par **Keycloak** (OAuth2/JWT), avec une **API Gateway** comme point d'entrée unique et une base de données **PostgreSQL** avec un schéma dédié par service.

### Objectifs métier

| Objectif | Description |
|----------|-------------|
| Suivi précis du temps | Auto-capture départ/arrivée/début/fin |
| Réduction saisie | De 15-20min à 5min par intervention |
| Élimination perte de données | De ~50% à 100% tracé avec mode offline |
| Gestion temps réel | De 24-48h à temps réel |
| Productité technicien | +20% interventions/jour |
| Portail client + OpenProject | Création auto de tickets |

### Volume cible

- 10-50 interventions/mois
- 15 techniciens
- 10 managers/admins
- 999+ clients

---

## 2. Stack technique

| Composant | Technologie | Version |
|-----------|-------------|---------|
| Runtime | Java | 25 |
| Framework | Spring Boot | 4.1.0 |
| Gateway | Spring Cloud Gateway (WebFlux) | 2025.1.2 |
| Auth | Keycloak (OAuth2/JWT) | 26.0.9 |
| Base de données | PostgreSQL | 16 |
| ORM | JPA / Hibernate | — |
| Migrations | Flyway | — |
| API Docs | SpringDoc OpenAPI | 3.0.3 |
| PDF | OpenPDF (LibrePDF) | 1.4.1 |
| Rate limiting | Redis + Resilience4j | — |
| Email | Spring Mail + Thymeleaf | — |
| Tests | JUnit 5 + Mockito | — |
| Build | Maven | Wrapper |
| Monitoring | Sentry (free tier) | — |
| CI/CD | GitHub Actions | — |

---

## 3. Architecture microservices

```
┌─────────────────────────────────────────────────────┐
│                   Frontend                           │
│        Angular (port 4200) / Mobile (port 8100)     │
└──────────────────────┬──────────────────────────────┘
                       │ HTTPS / JWT
                       ▼
┌─────────────────────────────────────────────────────┐
│              API Gateway (port 8080)                  │
│    Spring Cloud Gateway (WebFlux réactif)             │
│    Routage + Rate limiting (Redis) + CORS            │
└──┬───────┬───────┬───────┬───────┬───────┬──────────┘
   │       │       │       │       │       │
   ▼       ▼       ▼       ▼       ▼       ▼
┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐
│ Auth │ │Client│ │ Inter│ │ Media│ │ Notif│ │Report│
│ 8081 │ │ 8082 │ │ 8083 │ │ 8084 │ │ 8085 │ │ 8086 │
└──┬───┘ └──┬───┘ └──┬───┘ └──────┘ └──────┘ └──────┘
   │        │        │
   ▼        ▼        ▼
┌──────────────────────────────┐
│   PostgreSQL (port 5432)      │
│   Schémas: auth, client,     │
│   intervention, notification │
└──────────────────────────────┘

     ┌──────────────────┐
     │    Keycloak      │
     │   (port 8088)    │
     │ realm: ng-fields │
     └──────────────────┘
```

### Modules Maven

| Module | Artifact | Version |
|--------|----------|---------|
| Parent | `ng-fields-backend` | 1.0.0-SNAPSHOT |
| shared-lib | `ng-fields-shared-lib` | 1.0.0-SNAPSHOT |
| gateway-service | `gateway-service` | 1.0.0-SNAPSHOT |
| auth-service | `auth-service` | 1.0.0-SNAPSHOT |
| client-service | `client-service` | 1.0.0-SNAPSHOT |
| intervention-service | `intervention-service` | 1.0.0-SNAPSHOT |
| media-service | `media-service` | 1.0.0-SNAPSHOT |
| notification-service | `notification-service` | 1.0.0-SNAPSHOT |
| report-service | `report-service` | 1.0.0-SNAPSHOT |

### Compteur de fichiers

| Service | Java (main) | Java (test) | SQL | YAML | HTML |
|---------|-------------|-------------|-----|------|------|
| shared-lib | 1 | 0 | 0 | 0 | 0 |
| gateway-service | 4 | 1 | 0 | 3 | 0 |
| auth-service | 21 | 2 | 3 | 3 | 0 |
| client-service | 13 | 1 | 4 | 3 | 0 |
| intervention-service | 37 | 1 | 5 | 3 | 0 |
| media-service | 6 | 0 | 0 | 3 | 0 |
| notification-service | 6 | 1 | 0 | 4 | 1 |
| report-service | 7 | 1 | 0 | 3 | 0 |
| **TOTAL** | **95** | **6** | **12** | **22** | **1** |

---

## 4. Détail des services

### 4.1 shared-lib

**Description**: Bibliothèque partagée pour les services NG-Fields  
**Artifact**: `ng-fields-shared-lib`  
**Parent**: `spring-boot-starter-parent` 4.1.0

#### Fichiers

| Fichier | Chemin |
|---------|--------|
| `RealmRoleConverter.java` | `shared-lib/src/main/java/tg/ngstars/common/security/RealmRoleConverter.java` |

#### `RealmRoleConverter.java`

- **Classe**: `tg.ngstars.common.security.RealmRoleConverter`
- **Implémente**: `Converter<Jwt, Collection<GrantedAuthority>>`
- **Rôle**: Extrait les rôles du claim `realm_access` du JWT Keycloak et les mappe en `ROLE_*` Spring Security
- **Logique**: Lit `realm_access.roles` → préfixe `ROLE_` → uppercased → retourne `SimpleGrantedAuthority`

#### Dépendances

- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`

---

### 4.2 gateway-service

**Description**: Point d'entrée unique, routage, rate limiting, CORS, agrégation Swagger  
**Port**: 8080  
**Type**: Spring Cloud Gateway (WebFlux réactif)

#### Fichiers

| Fichier | Chemin |
|---------|--------|
| `GatewayServiceApplication.java` | `gateway-service/src/main/java/tg/ngstars/gateway/GatewayServiceApplication.java` |
| `SecurityConfig.java` | `gateway-service/src/main/java/tg/ngstars/gateway/config/SecurityConfig.java` |
| `RateLimitConfig.java` | `gateway-service/src/main/java/tg/ngstars/gateway/config/RateLimitConfig.java` |
| `KeycloakJwtAuthenticationConverter.java` | `gateway-service/src/main/java/tg/ngstars/gateway/config/KeycloakJwtAuthenticationConverter.java` |
| `KeycloakJwtAuthenticationConverterTest.java` | `gateway-service/src/test/java/tg/ngstars/gateway/config/KeycloakJwtAuthenticationConverterTest.java` |
| `application.yml` | `gateway-service/src/main/resources/application.yml` |
| `application-dev.yml` | `gateway-service/src/main/resources/application-dev.yml` |
| `application-prod.yml` | `gateway-service/src/main/resources/application-prod.yml` |

#### SecurityConfig.java

- **Classe**: `tg.ngstars.gateway.config.SecurityConfig`
- **Annotations**: `@Configuration`, `@EnableWebFluxSecurity`
- **Méthodes**:
  - `springSecurityFilterChain(ServerHttpSecurity http)` → `SecurityWebFilterChain`
    - CSRF désactivé
    - CORS configuré
    - Autorisations: `OPTIONS` permitted, `/actuator/health`, `/actuator/info`, `/api/public/**` permitted, tout le reste requiert authentification
    - OAuth2 Resource Server avec `KeycloakJwtAuthenticationConverter`
  - `corsConfigurationSource()` → `CorsConfigurationSource`
    - Origines: `*` (à restreindre en prod)
    - Méthodes: GET/POST/PUT/PATCH/DELETE/OPTIONS
    - Headers: `*`
    - Credentials: true

#### RateLimitConfig.java

- **Classe**: `tg.ngstars.gateway.config.RateLimitConfig`
- **Méthodes**:
  - `userKeyResolver()` → `KeyResolver` — Résout la clé par nom d'utilisateur authentifié (fallback: IP distante)
  - `remoteAddrKeyResolver()` → `KeyResolver` — Utilise toujours l'IP distante

#### KeycloakJwtAuthenticationConverter.java

- **Classe**: `tg.ngstars.gateway.config.KeycloakJwtAuthenticationConverter`
- **Implémente**: `Converter<Jwt, Mono<AbstractAuthenticationToken>>` (réactif)
- **Méthodes**:
  - `convert(Jwt jwt)` → `Mono<AbstractAuthenticationToken>` — Extrait les rôles, wrap en `JwtAuthenticationToken`
  - `extractRoles(Jwt jwt)` → `Collection<GrantedAuthority>` — Lit `realm_access.roles`, mappe en `ROLE_*`

#### Routes configurées

| Route ID | Chemin | Cible | Rate Limit (replenish/burst) | Key Resolver |
|----------|--------|-------|------------------------------|--------------|
| `auth-register` | `/api/public/register` | auth-service:8081 | 3/6 | remoteAddr |
| `auth-public` | `/api/public/**` | auth-service:8081 | none | — |
| `auth-admin` | `/api/admin/users/**` | auth-service:8081 | 10/20 | user |
| `auth-me` | `/api/users/me` | auth-service:8081 | none | — |
| `client-service` | `/api/clients/**` | client-service:8082 | 20/40 | user |
| `intervention-service` | `/api/interventions/**` | intervention-service:8083 | 30/60 | user |
| `sync-service` | `/api/sync/**` | intervention-service:8083 | 10/20 | user |
| `media-service` | `/api/media/**` | media-service:8084 | 20/40 | user |
| `notification-service` | `/api/notifications/**` | notification-service:8085 | 10/20 | user |
| `report-service` | `/api/reports/**` | report-service:8086 | 5/10 | user |

#### Configuration Resilience4j

- **CircuitBreaker**: Sliding window 10, min calls 5, failure threshold 50%, wait 10s, half-open 3 calls
- **TimeLimiter**: 5s timeout

#### Dépendances

- `spring-cloud-starter-gateway-server-webflux`
- `spring-cloud-starter-circuitbreaker-reactor-resilience4j`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-actuator`
- `spring-boot-starter-data-redis-reactive`
- `springdoc-openapi-starter-webflux-ui` 3.0.3
- `lombok`

---

### 4.3 auth-service

**Description**: Gestion des utilisateurs, rôles, authentification (délégation Keycloak), audit trail  
**Port**: 8081  
**Base de données**: PostgreSQL (schéma `auth`)

#### Fichiers

| Fichier | Chemin |
|---------|--------|
| `AuthServiceApplication.java` | `auth-service/src/main/java/tg/ngstars/auth/AuthServiceApplication.java` |
| `GlobalExceptionHandler.java` | `auth-service/src/main/java/tg/ngstars/auth/config/GlobalExceptionHandler.java` |
| `KeycloakAdminConfig.java` | `auth-service/src/main/java/tg/ngstars/auth/config/KeycloakAdminConfig.java` |
| `KeycloakProperties.java` | `auth-service/src/main/java/tg/ngstars/auth/config/KeycloakProperties.java` |
| `SecurityConfig.java` | `auth-service/src/main/java/tg/ngstars/auth/config/SecurityConfig.java` |
| `UserController.java` | `auth-service/src/main/java/tg/ngstars/auth/controller/UserController.java` |
| `CreateUserRequest.java` | `auth-service/src/main/java/tg/ngstars/auth/dto/CreateUserRequest.java` |
| `RoleAssignRequest.java` | `auth-service/src/main/java/tg/ngstars/auth/dto/RoleAssignRequest.java` |
| `UpdateProfileRequest.java` | `auth-service/src/main/java/tg/ngstars/auth/dto/UpdateProfileRequest.java` |
| `UpdateUserRequest.java` | `auth-service/src/main/java/tg/ngstars/auth/dto/UpdateUserRequest.java` |
| `UserResponse.java` | `auth-service/src/main/java/tg/ngstars/auth/dto/UserResponse.java` |
| `UserStatusRequest.java` | `auth-service/src/main/java/tg/ngstars/auth/dto/UserStatusRequest.java` |
| `ConflictException.java` | `auth-service/src/main/java/tg/ngstars/auth/exception/ConflictException.java` |
| `NotFoundException.java` | `auth-service/src/main/java/tg/ngstars/auth/exception/NotFoundException.java` |
| `AuditLog.java` | `auth-service/src/main/java/tg/ngstars/auth/model/AuditLog.java` |
| `User.java` | `auth-service/src/main/java/tg/ngstars/auth/model/User.java` |
| `AuditLogRepository.java` | `auth-service/src/main/java/tg/ngstars/auth/repository/AuditLogRepository.java` |
| `UserRepository.java` | `auth-service/src/main/java/tg/ngstars/auth/repository/UserRepository.java` |
| `AuditService.java` | `auth-service/src/main/java/tg/ngstars/auth/service/AuditService.java` |
| `SecurityUtils.java` | `auth-service/src/main/java/tg/ngstars/auth/service/SecurityUtils.java` |
| `UserService.java` | `auth-service/src/main/java/tg/ngstars/auth/service/UserService.java` |
| `AuditServiceTest.java` | `auth-service/src/test/java/tg/ngstars/auth/service/AuditServiceTest.java` |
| `UserServiceTest.java` | `auth-service/src/test/java/tg/ngstars/auth/service/UserServiceTest.java` |

#### Endpoints

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/public/register` | Public | Auto-inscription (force rôle CLIENT_PORTAL) |
| GET | `/api/public/health` | Public | Health check |
| GET | `/api/users/me` | Auth | Profil utilisateur courant |
| PUT | `/api/users/me` | Auth | Mise à jour profil |
| POST | `/api/admin/users` | ADMIN | Création utilisateur |
| GET | `/api/admin/users` | ADMIN | Liste utilisateurs (paginée) |
| GET | `/api/admin/users/{id}` | ADMIN | Détail utilisateur |
| PUT | `/api/admin/users/{id}` | ADMIN | Modification |
| DELETE | `/api/admin/users/{id}` | ADMIN | Désactivation (soft delete) |
| PATCH | `/api/admin/users/{keycloakId}/roles` | ADMIN | Changement rôle |
| PATCH | `/api/admin/users/{keycloakId}/status` | ADMIN | Activer/désactiver |
| POST | `/api/admin/users/{keycloakId}/reset-password` | ADMIN | Réinitialisation mot de passe |

#### UserService — Méthodes clés

| Méthode | Transaction | Description |
|---------|-------------|-------------|
| `createUser(CreateUserRequest, String createdBy, String ip)` | `@Transactional` | Vérifie doublon username/email. Crée user dans Keycloak (retry sur 409). Crée entité locale. Assigne rôle. Audite. Rollback: supprime user Keycloak si échec DB. |
| `getAllUsers(Pageable)` | `@Transactional(readOnly=true)` | Liste paginée |
| `getUser(UUID)` | `@Transactional(readOnly=true)` | Find by ID ou NotFoundException |
| `updateUser(UUID, UpdateUserRequest, String updatedBy)` | `@Transactional` | MAJ DB + Keycloak. Reset password si fourni. Réassigne rôle si changé. Audite. |
| `deleteUser(UUID, String deletedBy)` | `@Transactional` | Soft delete: `active=false` DB + `enabled=false` Keycloak. Audite. |
| `assignRole(UUID keycloakId, String newRole, String adminId)` | `@Transactional` | Supprime tous les rôles métier, ajoute le nouveau dans Keycloak. MAJ DB. Audite. |
| `updateUserStatus(UUID keycloakId, boolean enabled, String adminId)` | `@Transactional` | Toggle `active` DB + `enabled` Keycloak. Audite. |
| `sendPasswordReset(UUID keycloakId, String adminId)` | `@Transactional` | Appelle `executeActionsEmail(UPDATE_PASSWORD)` Keycloak. Audite. |
| `getProfile(UUID keycloakId)` | `@Transactional(readOnly=true)` | Find by keycloakId |
| `updateProfile(UUID keycloakId, UpdateProfileRequest)` | `@Transactional` | MAJ first/last name DB + Keycloak. Audite. |
| `registerClient(CreateUserRequest, String ip)` | — | Appelle `createUser` avec rôle `CLIENT_PORTAL` et `createdBy="SELF_REGISTER"`. |

#### Modèle de données

**User.java** (Entité)

| Champ | Type | Contraintes |
|-------|------|-------------|
| `id` | UUID | @Id |
| `keycloakId` | String | unique |
| `username` | String(50) | unique |
| `email` | String | unique |
| `firstName` | String | — |
| `lastName` | String | — |
| `role` | String | — |
| `phone` | String | — |
| `active` | boolean | default true |
| `version` | Long | @Version (optimistic locking) |
| `createdAt` | OffsetDateTime | — |
| `updatedAt` | OffsetDateTime | — |

**AuditLog.java** (Entité)

| Champ | Type |
|-------|------|
| `id` | UUID |
| `userId` | UUID |
| `action` | String |
| `resource` | String |
| `resourceId` | String |
| `details` | String |
| `ipAddress` | String |
| `createdAt` | OffsetDateTime |

#### DTOs

**CreateUserRequest.java** (record)

| Champ | Validation |
|-------|------------|
| `username` | @NotBlank @Size(3-50) |
| `email` | @NotBlank @Email |
| `firstName` | @NotBlank @Size(max=100) |
| `lastName` | @NotBlank @Size(max=100) |
| `password` | @NotBlank @Size(min=8) @Pattern(majuscule, minuscule, chiffre, spécial) |
| `role` | @NotBlank @Pattern("ADMIN\|MANAGER\|TECHNICIAN\|CLIENT_PORTAL") |
| `phone` | optional |

**UserResponse.java** (record)

Champs: `id`, `keycloakId`, `username`, `email`, `firstName`, `lastName`, `role`, `phone`, `active`, `createdAt`, `updatedAt`

#### Migrations SQL

| Version | Fichier | Description |
|---------|---------|-------------|
| V1 | `V1__init_schema.sql` | Tables `users` et `audit_logs` |
| V2 | `V2__add_audit_logs_index.sql` | Index sur `audit_logs(user_id)` et `audit_logs(created_at)` |
| V3 | `V3__add_version_to_users.sql` | Ajout `version BIGINT NOT NULL DEFAULT 0` à `users` |

#### Dépendances

- `ng-fields-shared-lib` 1.0.0-SNAPSHOT
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-validation`
- `spring-boot-starter-actuator`
- `spring-boot-starter-flyway`
- `flyway-database-postgresql`
- `postgresql` (runtime)
- `springdoc-openapi-starter-webmvc-ui` 3.0.3
- `keycloak-admin-client` 26.0.9
- `lombok`

---

### 4.4 client-service

**Description**: Gestion des fiches clients  
**Port**: 8082  
**Base de données**: PostgreSQL (schéma `client`)

#### Fichiers

| Fichier | Chemin |
|---------|--------|
| `ClientServiceApplication.java` | `client-service/src/main/java/tg/ngstars/client/ClientServiceApplication.java` |
| `GlobalExceptionHandler.java` | `client-service/src/main/java/tg/ngstars/client/config/GlobalExceptionHandler.java` |
| `SecurityConfig.java` | `client-service/src/main/java/tg/ngstars/client/config/SecurityConfig.java` |
| `ClientController.java` | `client-service/src/main/java/tg/ngstars/client/controller/ClientController.java` |
| `ClientResponse.java` | `client-service/src/main/java/tg/ngstars/client/dto/ClientResponse.java` |
| `CreateClientRequest.java` | `client-service/src/main/java/tg/ngstars/client/dto/CreateClientRequest.java` |
| `UpdateClientRequest.java` | `client-service/src/main/java/tg/ngstars/client/dto/UpdateClientRequest.java` |
| `ConflictException.java` | `client-service/src/main/java/tg/ngstars/client/exception/ConflictException.java` |
| `NotFoundException.java` | `client-service/src/main/java/tg/ngstars/client/exception/NotFoundException.java` |
| `Client.java` | `client-service/src/main/java/tg/ngstars/client/model/Client.java` |
| `ClientRepository.java` | `client-service/src/main/java/tg/ngstars/client/repository/ClientRepository.java` |
| `ClientService.java` | `client-service/src/main/java/tg/ngstars/client/service/ClientService.java` |
| `ReferenceGeneratorService.java` | `client-service/src/main/java/tg/ngstars/client/service/ReferenceGeneratorService.java` |
| `ClientServiceTest.java` | `client-service/src/test/java/tg/ngstars/client/service/ClientServiceTest.java` |

#### Endpoints

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/clients` | ADMIN | Création client |
| GET | `/api/clients` | ADMIN/MANAGER/TECHNICIAN | Liste paginée |
| GET | `/api/clients/search?q=` | ADMIN/MANAGER/TECHNICIAN | Recherche trigram (nom, contact, email) |
| GET | `/api/clients/{id}` | ADMIN/MANAGER/TECHNICIAN | Détail client |
| PUT | `/api/clients/{id}` | ADMIN | Modification |
| DELETE | `/api/clients/{id}` | ADMIN | Désactivation (soft delete) |

#### ClientService — Méthodes clés

| Méthode | Transaction | Description |
|---------|-------------|-------------|
| `createClient(CreateClientRequest, String createdBy)` | `@Transactional` | Vérifie doublon email. Génère référence séquentielle (`CLT-XXXX`). Crée entité. Sauvegarde. |
| `listClients(int page, int size)` | `@Transactional(readOnly=true)` | Clients actifs triés par companyName |
| `getClient(UUID id)` | `@Transactional(readOnly=true)` | Find by ID ou NotFoundException |
| `updateClient(UUID id, UpdateClientRequest)` | `@Transactional` | Vérifie doublon email si modifié. MAJ tous les champs. |
| `deactivateClient(UUID id)` | `@Transactional` | `active=false` |
| `searchClients(String query, int page, int size)` | `@Transactional(readOnly=true)` | Recherche trigram via repository |

#### ReferenceGeneratorService

- **Méthode**: `generateNextReference()` → `String`
- **Logique**: Utilise la séquence native `clientRefSeq` pour générer la valeur suivante
- **Format**: `"CLT-XXXX"` (4 chiffres, zero-padded)

#### Modèle de données

**Client.java** (Entité)

| Champ | Type | Contraintes |
|-------|------|-------------|
| `id` | UUID | @Id |
| `reference` | String(20) | unique |
| `companyName` | String(200) | — |
| `contactName` | String(150) | — |
| `email` | String(150) | unique |
| `phone` | String(30) | — |
| `address` | TEXT | — |
| `latitude` | Double | -90 à 90 |
| `longitude` | Double | -180 à 180 |
| `active` | boolean | default true |
| `version` | Long | @Version |
| `createdBy` | String(100) | — |
| `createdAt` | OffsetDateTime | — |
| `updatedAt` | OffsetDateTime | — |

#### DTOs

**CreateClientRequest.java** (record)

| Champ | Validation |
|-------|------------|
| `companyName` | @NotBlank @Size(max=200) |
| `contactName` | @Size(max=150) |
| `email` | @NotBlank @Email @Size(max=150) |
| `phone` | @Size(max=30) |
| `address` | — |
| `latitude` | @DecimalMin(-90) @DecimalMax(90) |
| `longitude` | @DecimalMin(-180) @DecimalMax(180) |

**ClientResponse.java** (record)

Champs: `id`, `reference`, `companyName`, `contactName`, `email`, `phone`, `address`, `latitude`, `longitude`, `active`, `createdAt`

#### Migrations SQL

| Version | Fichier | Description |
|---------|---------|-------------|
| V1 | `V1__init_schema.sql` | Table `clients` avec index sur email, active, company_name |
| V2 | `V2__add_client_ref_sequence.sql` | `CREATE SEQUENCE client_ref_seq START 1` |
| V3 | `V3__add_trgm_search_index.sql` | Extension `pg_trgm` + index GIN trigram |
| V4 | `V4__add_version_to_clients.sql` | Ajout `version BIGINT NOT NULL DEFAULT 0` |

#### Dépendances

- `ng-fields-shared-lib` 1.0.0-SNAPSHOT
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-validation`
- `spring-boot-starter-actuator`
- `spring-boot-starter-flyway`
- `flyway-database-postgresql`
- `postgresql` (runtime)
- `springdoc-openapi-starter-webmvc-ui` 3.0.3
- `lombok`

---

### 4.5 intervention-service

**Description**: Cœur métier — gestion complète des interventions terrain  
**Port**: 8083  
**Base de données**: PostgreSQL (schéma `intervention`)  
**Taille**: Le plus grand service — 38 fichiers Java, 5 migrations SQL

#### Fichiers

| Fichier | Chemin |
|---------|--------|
| `InterventionServiceApplication.java` | `intervention-service/src/main/java/tg/ngstars/interv/InterventionServiceApplication.java` |
| `MediaClient.java` | `intervention-service/src/main/java/tg/ngstars/interv/client/MediaClient.java` |
| `GlobalExceptionHandler.java` | `intervention-service/src/main/java/tg/ngstars/interv/config/GlobalExceptionHandler.java` |
| `MediaClientConfig.java` | `intervention-service/src/main/java/tg/ngstars/interv/config/MediaClientConfig.java` |
| `SecurityConfig.java` | `intervention-service/src/main/java/tg/ngstars/interv/config/SecurityConfig.java` |
| `InterventionController.java` | `intervention-service/src/main/java/tg/ngstars/interv/controller/InterventionController.java` |
| `PhotoController.java` | `intervention-service/src/main/java/tg/ngstars/interv/controller/PhotoController.java` |
| `SignatureController.java` | `intervention-service/src/main/java/tg/ngstars/interv/controller/SignatureController.java` |
| `SyncController.java` | `intervention-service/src/main/java/tg/ngstars/interv/controller/SyncController.java` |
| `CreateInterventionRequest.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/CreateInterventionRequest.java` |
| `InterventionResponse.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/InterventionResponse.java` |
| `ItemRequest.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/ItemRequest.java` |
| `ItemResponse.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/ItemResponse.java` |
| `PhotoResponse.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/PhotoResponse.java` |
| `SignatureRequest.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/SignatureRequest.java` |
| `SyncRequest.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/SyncRequest.java` |
| `UpdateBillingRequest.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateBillingRequest.java` |
| `UpdateDiagnosisRequest.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateDiagnosisRequest.java` |
| `UpdateEquipmentRequest.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateEquipmentRequest.java` |
| `UpdateRecommendationsRequest.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateRecommendationsRequest.java` |
| `UpdateResultRequest.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateResultRequest.java` |
| `UpdateScheduleRequest.java` | `intervention-service/src/main/java/tg/ngstars/interv/dto/UpdateScheduleRequest.java` |
| `ForbiddenException.java` | `intervention-service/src/main/java/tg/ngstars/interv/exception/ForbiddenException.java` |
| `MediaServiceException.java` | `intervention-service/src/main/java/tg/ngstars/interv/exception/MediaServiceException.java` |
| `NotFoundException.java` | `intervention-service/src/main/java/tg/ngstars/interv/exception/NotFoundException.java` |
| `Intervention.java` | `intervention-service/src/main/java/tg/ngstars/interv/model/Intervention.java` |
| `InterventionItem.java` | `intervention-service/src/main/java/tg/ngstars/interv/model/InterventionItem.java` |
| `InterventionPhoto.java` | `intervention-service/src/main/java/tg/ngstars/interv/model/InterventionPhoto.java` |
| `PhotoType.java` | `intervention-service/src/main/java/tg/ngstars/interv/model/PhotoType.java` |
| `InterventionPhotoRepository.java` | `intervention-service/src/main/java/tg/ngstars/interv/repository/InterventionPhotoRepository.java` |
| `InterventionRepository.java` | `intervention-service/src/main/java/tg/ngstars/interv/repository/InterventionRepository.java` |
| `InterventionService.java` | `intervention-service/src/main/java/tg/ngstars/interv/service/InterventionService.java` |
| `InterventionStatusService.java` | `intervention-service/src/main/java/tg/ngstars/interv/service/InterventionStatusService.java` |
| `PdfService.java` | `intervention-service/src/main/java/tg/ngstars/interv/service/PdfService.java` |
| `PhotoService.java` | `intervention-service/src/main/java/tg/ngstars/interv/service/PhotoService.java` |
| `SecurityUtils.java` | `intervention-service/src/main/java/tg/ngstars/interv/service/SecurityUtils.java` |
| `SignatureService.java` | `intervention-service/src/main/java/tg/ngstars/interv/service/SignatureService.java` |
| `InterventionServiceTest.java` | `intervention-service/src/test/java/tg/ngstars/interv/service/InterventionServiceTest.java` |

#### Endpoints — InterventionController

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/interventions` | Admin/Manager/Technicien | Création intervention |
| GET | `/api/interventions` | Admin/Manager/Technicien | Liste (filtrable par `status`, `technicianId`) |
| GET | `/api/interventions/{id}` | Admin/Manager/Technicien | Détail |
| PUT | `/api/interventions/{id}` | Admin/Manager/Technicien | Mise à jour complète |
| DELETE | `/api/interventions/{id}` | Admin/Manager/Technicien | Désactivation |
| GET | `/api/interventions/{id}/pdf` | Admin/Manager/Technicien | Génération PDF |
| GET | `/api/interventions/by-client/{clientId}` | Admin/Manager/Technicien | Interventions d'un client |
| PATCH | `/api/interventions/{id}/schedule` | Propriétaire* | Horaires |
| PATCH | `/api/interventions/{id}/equipment` | Propriétaire* | Équipement |
| PATCH | `/api/interventions/{id}/diagnosis` | Propriétaire* | Diagnostic |
| PATCH | `/api/interventions/{id}/result` | Propriétaire* | Résultat |
| PATCH | `/api/interventions/{id}/recommendations` | Propriétaire* | Recommandations |
| PATCH | `/api/interventions/{id}/billing` | Propriétaire* | Facturation |
| POST | `/api/interventions/{id}/items` | Propriétaire* | Ajout pièce |
| PUT | `/api/interventions/{id}/items/{itemId}` | Propriétaire* | Modif pièce |
| DELETE | `/api/interventions/{id}/items/{itemId}` | Propriétaire* | Suppr pièce |
| POST | `/api/interventions/{id}/close` | Propriétaire* | Clôture (auto si 3 signatures) |

\* *Propriétaire = technicien assigné, sauf ADMIN/MANAGER qui peuvent modifier toute intervention.*

#### Endpoints — SyncController

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/sync/interventions` | Admin/Manager/Technicien | Sync mobile (création ou MAJ) |

#### Endpoints — PhotoController

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/interventions/{id}/photos` | TECHNICIAN/MANAGER/ADMIN | Upload photo (multipart) |
| GET | `/api/interventions/{id}/photos` | isAuthenticated | Liste photos |
| GET | `/api/interventions/{id}/photos/type/{type}` | isAuthenticated | Filtrer par type (BEFORE/AFTER) |
| DELETE | `/api/interventions/{id}/photos/{photoId}` | TECHNICIAN/MANAGER/ADMIN | Supprimer photo |

**Types MIME autorisés**: `image/jpeg`, `image/png`, `image/webp`  
**Limite**: 5 photos par catégorie

#### Endpoints — SignatureController

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/interventions/{id}/signatures/client` | TECHNICIAN/MANAGER/ADMIN | Signature client |
| POST | `/api/interventions/{id}/signatures/technician` | TECHNICIAN/MANAGER/ADMIN | Signature technicien |
| POST | `/api/interventions/{id}/signatures/manager` | MANAGER/ADMIN | Signature manager (auto-complète si 3 signatures) |

#### InterventionService — Méthodes clés

| Méthode | Transaction | Description |
|---------|-------------|-------------|
| `createIntervention(request, userId)` | `@Transactional` | Crée intervention + items. Vérifie référence unique. Calcule totalCost. |
| `getInterventions(status, technicianId, pageable)` | `@Transactional(readOnly=true)` | Filtre par technicien et/ou statut |
| `getIntervention(id, userId, isAdminOrManager)` | `@Transactional(readOnly=true)` | Fetch unique avec vérification ownership |
| `updateIntervention(id, request, userId, isAdminOrManager)` | `@Transactional` | MAJ complète + items (clear + rebuild) |
| `deleteIntervention(id, userId, isAdminOrManager)` | `@Transactional` | Soft delete (`active=false`) |
| `getClientInterventions(clientId, userId, isAdminOrManager, pageable)` | `@Transactional(readOnly=true)` | Liste par clientId |
| `generatePdf(id, userId, isAdminOrManager)` | — | Retourne `byte[]` PDF |
| `generatePdfToStream(id, userId, isAdminOrManager, OutputStream)` | — | Stream PDF vers output |
| `updateSchedule(id, request, userId, isAdminOrManager)` | `@Transactional` | MAJ horaires. Auto-calcule `durationMinutes` |
| `updateEquipment(id, request, userId, isAdminOrManager)` | `@Transactional` | MAJ équipement + openproject fields |
| `updateDiagnosis(id, request, userId, isAdminOrManager)` | `@Transactional` | MAJ diagnostic + workDone |
| `updateResult(id, request, userId, isAdminOrManager)` | `@Transactional` | MAJ résultat |
| `updateRecommendations(id, request, userId, isAdminOrManager)` | `@Transactional` | MAJ recommandations |
| `updateBilling(id, request, userId, isAdminOrManager)` | `@Transactional` | MAJ facturation |
| `addItem(id, request, userId, isAdminOrManager)` | `@Transactional` | Ajoute pièce, recalcule totalCost |
| `updateItem(interventionId, itemId, request, userId, isAdminOrManager)` | `@Transactional` | MAJ pièce, recalcule |
| `removeItem(interventionId, itemId, userId, isAdminOrManager)` | `@Transactional` | Supprime pièce, recalcule totalCost |
| `closeIntervention(id, userId, isAdminOrManager)` | `@Transactional` | Délègue à InterventionStatusService |
| `syncFromMobile(request, userId, isAdminOrManager)` | `@Transactional` | Upsert par `localId`: si existe → MAJ, sinon → création |

#### InterventionStatusService

- **Méthode**: `closeIntervention(Intervention, UUID userId, boolean isAdminOrManager)`
- **Logique**: Vérifie ownership. Vérifie pas déjà COMPLETED. Exige 3 signatures (client, technicien, manager). Définit `status="COMPLETED"` et `signedAt=now`.

#### PdfService

- **Méthode**: `write(Intervention, OutputStream)` — Génère PDF avec: titre, référence, infos client, équipement, diagnostic, tableau des pièces
- **Méthode**: `generate(Intervention)` → `byte[]` — Écrit dans ByteArrayOutputStream

#### PhotoService

- **Limite**: 5 photos par catégorie (BEFORE/AFTER)
- **Upload**: Via `MediaClient` (inter-service REST)
- **Cleanup**: `TransactionSynchronization` callback pour supprimer fichier si rollback

#### SignatureService

- **Upload**: Base64 → Media service
- **Auto-complète**: Si 3 signatures présentes après signature manager → intervention COMPLETED

#### Modèle de données

**Intervention.java** (Entité — 37 champs)

| Catégorie | Champs |
|-----------|--------|
| Identifiant | `id` (UUID), `reference` (unique), `localId` (unique, sync mobile) |
| Client | `clientId`, `clientName`, `clientEmail`, `clientPhone`, `clientAddress` |
| Équipement | `equipmentType`, `equipmentBrand`, `equipmentModel`, `equipmentSerial`, `equipmentLocation` |
| Problème | `reportedIssue`, `openprojectTicketId`, `openprojectTicketUrl` |
| Diagnostic | `diagnosis`, `workDone` |
| Statut | `status` (default "PENDING"), `interventionDate` |
| Affectation | `createdBy` (UUID), `assignedTo` (UUID) |
| Localisation | `siteAddress`, `siteCity` |
| Coût | `estimatedCost` (BigDecimal), `totalCost` (BigDecimal) |
| Signatures | `clientSignature` (TEXT), `technicianSignature` (TEXT), `managerSignature` (TEXT), `signedAt` |
| Horaires | `departureTime`, `arrivalTime`, `startTime`, `endTime`, `durationMinutes` (Integer) |
| Résultat | `result` (20), `recommendations` (TEXT) |
| Facturation | `billable` (default true), `billingAmount` (BigDecimal), `billingNotes` (TEXT) |
| Métadonnées | `notes`, `active` (default true), `version` (Integer @Version), `createdAt`, `updatedAt` |
| Relations | `items` (OneToMany, cascade ALL, orphanRemoval, LAZY) |

**InterventionItem.java** (Entité)

| Champ | Type |
|-------|------|
| `id` | UUID |
| `intervention` | ManyToOne LAZY |
| `type` | String |
| `description` | String |
| `quantity` | Integer (default 1) |
| `unitPrice` | BigDecimal (default ZERO) |
| `total` | BigDecimal (default ZERO, calculé = unitPrice × quantity) |
| `version` | Integer @Version |
| `createdAt` | OffsetDateTime |

**InterventionPhoto.java** (Entité)

| Champ | Type |
|-------|------|
| `id` | UUID @GeneratedValue |
| `intervention` | ManyToOne LAZY |
| `url` | TEXT |
| `type` | PhotoType (BEFORE, AFTER) |
| `latitude` | Double |
| `longitude` | Double |
| `takenAt` | Instant |
| `originalFilename` | String(200) |
| `version` | Integer @Version |
| `createdAt` | Instant |

**PhotoType.java** (Enum)

Valeurs: `BEFORE`, `AFTER`

#### DTOs principaux

**CreateInterventionRequest.java** (record)

Champs: `reference`, `clientId` (UUID), `clientName`, `clientEmail`, `clientPhone`, `clientAddress`, `equipmentType`, `equipmentBrand`, `equipmentModel`, `equipmentSerial`, `equipmentLocation`, `reportedIssue`, `openprojectTicketId`, `openprojectTicketUrl`, `diagnosis`, `workDone`, `status`, `interventionDate`, `assignedTo` (UUID), `siteAddress`, `siteCity`, `estimatedCost` (BigDecimal), `notes`, `items` (List of `CreateItemRequest`)

**InterventionResponse.java** (record)

38 champs miroir de l'entité + `items` (List of `ItemResponse`)

#### Migrations SQL

| Version | Fichier | Description |
|---------|---------|-------------|
| V1 | `V1__init_schema.sql` | Tables `interventions` et `intervention_items` avec toutes les colonnes de base |
| V2 | `V2__add_photos_and_signatures.sql` | Ajout `manager_signature`, création `intervention_photos` avec index |
| V3 | `V3__add_intervention_sections.sql` | Colonnes horaires (departure/arrival/start/end, duration), work_done, result, recommendations, billable, billing_amount, billing_notes, local_id, equipment_location, openproject fields. Index sur status, assigned_to, local_id |
| V4 | `V4__add_client_id_index.sql` | Index sur `client_id` |
| V5 | `V5__add_version_columns.sql` | Ajout `version INTEGER NOT NULL DEFAULT 0` aux 3 tables |

#### Dépendances

- `ng-fields-shared-lib` 1.0.0-SNAPSHOT
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-validation`
- `spring-boot-starter-mail`
- `spring-boot-starter-actuator`
- `spring-boot-starter-flyway`
- `flyway-database-postgresql`
- `postgresql` (runtime)
- `springdoc-openapi-starter-webmvc-ui` 3.0.3
- `openpdf` 1.4.1 (`com.github.librepdf`)
- `lombok`

---

### 4.6 media-service

**Description**: Stockage et distribution de fichiers (photos, signatures, PDF)  
**Port**: 8084  
**Base de données**: Aucune — stockage sur disque local (`./uploads`)

#### Fichiers

| Fichier | Chemin |
|---------|--------|
| `MediaServiceApplication.java` | `media-service/src/main/java/tg/ngstars/media/MediaServiceApplication.java` |
| `GlobalExceptionHandler.java` | `media-service/src/main/java/tg/ngstars/media/config/GlobalExceptionHandler.java` |
| `MediaProperties.java` | `media-service/src/main/java/tg/ngstars/media/config/MediaProperties.java` |
| `SecurityConfig.java` | `media-service/src/main/java/tg/ngstars/media/config/SecurityConfig.java` |
| `FileController.java` | `media-service/src/main/java/tg/ngstars/media/controller/FileController.java` |
| `FileService.java` | `media-service/src/main/java/tg/ngstars/media/service/FileService.java` |

#### Endpoints

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/media/upload` | ADMIN/MANAGER/TECHNICIAN | Upload fichier (multipart) |
| POST | `/api/media/upload-base64` | ADMIN/MANAGER/TECHNICIAN | Upload image base64 |
| GET | `/api/media/{filename}` | — | Téléchargement (inline) |
| DELETE | `/api/media/{filename}` | ADMIN/MANAGER/TECHNICIAN | Suppression |

#### FileService — Méthodes clés

| Méthode | Description |
|---------|-------------|
| `init()` | `@PostConstruct`. Crée répertoire uploads. Charge `.owners.json` si existe. |
| `storeBytes(byte[], String ext, String userId)` → String | Génère filename UUID, écrit bytes, track ownership, persiste. Protection path traversal. |
| `store(MultipartFile, String userId)` → String | Valide extension contre allowlist, génère UUID, copie stream, track ownership. |
| `load(String filename)` → Path | Sanitise filename (anti path traversal), vérifie existence. |
| `delete(String filename, String userId)` | Valide ownership via `fileOwners` map, supprime fichier, MAJ tracking. |

**Extensions autorisées**: `.jpg`, `.jpeg`, `.png`, `.gif`, `.webp`, `.pdf`

**Sécurité**: Vérification anti-path-traversal (`file.startsWith(uploadPath)`)  
**Ownership**: Tracking via fichier `.owners.json` (ConcurrentHashMap)

#### MediaProperties.java

- **Type**: `record` avec `@ConfigurationProperties(prefix="media")`
- **Champs**: `uploadDir` (String)

#### Dépendances

- `ng-fields-shared-lib` 1.0.0-SNAPSHOT
- `spring-boot-starter-web`
- `spring-boot-starter-json`
- `spring-boot-starter-validation`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-actuator`
- `springdoc-openapi-starter-webmvc-ui` 3.0.3
- `lombok`

---

### 4.7 notification-service

**Description**: Envoi d'emails (Mail + Thymeleaf)  
**Port**: 8085  
**Base de données**: Aucune  
**Statut**: Fonctionnel (squelette implémenté)

#### Fichiers

| Fichier | Chemin |
|---------|--------|
| `NotificationServiceApplication.java` | `notification-service/src/main/java/tg/ngstars/notification/NotificationServiceApplication.java` |
| `GlobalExceptionHandler.java` | `notification-service/src/main/java/tg/ngstars/notification/config/GlobalExceptionHandler.java` |
| `SecurityConfig.java` | `notification-service/src/main/java/tg/ngstars/notification/config/SecurityConfig.java` |
| `NotificationController.java` | `notification-service/src/main/java/tg/ngstars/notification/controller/NotificationController.java` |
| `EmailRequest.java` | `notification-service/src/main/java/tg/ngstars/notification/dto/EmailRequest.java` |
| `EmailService.java` | `notification-service/src/main/java/tg/ngstars/notification/service/EmailService.java` |
| `NotificationServiceApplicationTests.java` | `notification-service/src/test/java/tg/ngstars/notification/NotificationServiceApplicationTests.java` |
| `intervention-notification.html` | `notification-service/src/main/resources/templates/email/intervention-notification.html` |

#### Endpoints

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/notifications/email` | ADMIN/MANAGER/TECHNICIAN | Envoi email (retourne 202 ACCEPTED) |

#### EmailService

- **Méthode**: `send(EmailRequest)`
- **Logique**:
  1. Valide template contre allowlist (`intervention-notification`, `password-reset`)
  2. Construit contexte Thymeleaf avec variables (`interventionRef`, `clientName`, `equipmentType`, `status`, `assignedTo`)
  3. Traite template `email/<template>`
  4. **Retry manuel**: 3 tentatives avec backoff exponentiel (1s, 2s, 3s)
  5. Envoie via `MimeMessageHelper` (HTML, UTF-8)
  6. Lève RuntimeException après épuisement des retries

#### EmailRequest.java (record)

| Champ | Validation |
|-------|------------|
| `to` | @NotBlank @Email |
| `subject` | @NotBlank |
| `template` | @NotBlank |
| `interventionRef` | — |
| `clientName` | — |
| `equipmentType` | — |
| `status` | — |
| `assignedTo` | — |

#### Template Thymeleaf

`intervention-notification.html` — Email HTML avec: référence, client, équipement, statut, assigné. Footer: "Email automatique - NG-STARs Field Service"

#### Dépendances

- `ng-fields-shared-lib` 1.0.0-SNAPSHOT
- `spring-boot-starter-actuator`
- `spring-boot-starter-mail`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-thymeleaf`
- `spring-boot-starter-validation`
- `spring-boot-starter-web`
- `springdoc-openapi-starter-webmvc-ui` 3.0.3
- `lombok`

---

### 4.8 report-service

**Description**: Génération de rapports (CSV)  
**Port**: 8086  
**Base de données**: Aucune (lit depuis intervention-service via HTTP)  
**Statut**: Fonctionnel (CSV export implémenté)

#### Fichiers

| Fichier | Chemin |
|---------|--------|
| `ReportServiceApplication.java` | `report-service/src/main/java/tg/ngstars/report/ReportServiceApplication.java` |
| `InterventionClient.java` | `report-service/src/main/java/tg/ngstars/report/client/InterventionClient.java` |
| `GlobalExceptionHandler.java` | `report-service/src/main/java/tg/ngstars/report/config/GlobalExceptionHandler.java` |
| `SecurityConfig.java` | `report-service/src/main/java/tg/ngstars/report/config/SecurityConfig.java` |
| `ReportController.java` | `report-service/src/main/java/tg/ngstars/report/controller/ReportController.java` |
| `InterventionReportDto.java` | `report-service/src/main/java/tg/ngstars/report/dto/InterventionReportDto.java` |
| `ReportService.java` | `report-service/src/main/java/tg/ngstars/report/service/ReportService.java` |
| `ReportServiceApplicationTests.java` | `report-service/src/test/java/tg/ngstars/report/ReportServiceApplicationTests.java` |

#### Endpoints

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| GET | `/api/reports/interventions/csv` | ADMIN/MANAGER | Export CSV (streaming) |

#### ReportService

- **Méthode**: `exportInterventionsCsvStream()` → `StreamingResponseBody`
- **Logique**:
  1. Écrit en-tête CSV (17 colonnes)
  2. Récupère jusqu'à 10 000 interventions depuis intervention-service
  3. Écrit chaque ligne en CSV
- **Protection CSV injection** (CWE-1236): Préfixe tab pour valeurs commençant par `=`, `+`, `-`, `@`

#### InterventionClient

- **Type**: `@Component` avec `RestClient`
- **Config**: Base URL `${intervention-service.url}` (default `http://localhost:8083/api/interventions`)
- **Timeouts**: 5s connect, 10s read
- **Sécurité**: Propage le JWT Bearer token pour les appels inter-services

#### InterventionReportDto.java (record)

Champs: `id`, `reference`, `clientName`, `clientEmail`, `clientPhone`, `equipmentType`, `equipmentBrand`, `equipmentModel`, `reportedIssue`, `diagnosis`, `workDone`, `status`, `assignedTo` (UUID), `result`, `billable` (Boolean), `billingAmount` (BigDecimal), `createdAt`, `updatedAt`

#### Dépendances

- `ng-fields-shared-lib` 1.0.0-SNAPSHOT
- `spring-boot-starter-actuator`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-web`
- `springdoc-openapi-starter-webmvc-ui` 3.0.3
- `lombok`

---

## 5. Base de données

### Architecture

- **PostgreSQL 16** à localhost:5432
- **2 bases de données**: `keycloak` (80 tables propres) + `ng_fields` (3 schémas applicatifs)
- **Schéma par service**: Isolation des données entre microservices
- **Extensions**: `uuid-ossp`, `pgcrypto`, `unaccent`

### Schémas

| Schéma | Service | Tables |
|--------|---------|--------|
| `auth` | auth-service | `users`, `audit_logs` |
| `client` | client-service | `clients` |
| `intervention` | intervention-service | `interventions`, `intervention_items`, `intervention_photos` |
| `notification` | notification-service | (à créer) |

### Rôles PostgreSQL

| Rôle | Password | Conn Limit | Usage |
|------|----------|-----------|-------|
| `keycloak_user` | `Keycloak_Pg_2026!` | 20 | Keycloak IAM |
| `ng_fields_user` | `Pg_ng-fields1234` | 50 | Spring Boot API |
| `ng_fields_test_user` | `NgFieldsTest_Pg_2026!` | 10 | Tests d'intégration |

### Migrations Flyway

| Service | Version | Fichier | Description |
|---------|---------|---------|-------------|
| Auth | V1 | `V1__init_schema.sql` | Tables `users` et `audit_logs` |
| Auth | V2 | `V2__add_audit_logs_index.sql` | Index audit_logs |
| Auth | V3 | `V3__add_version_to_users.sql` | Optimistic locking users |
| Client | V1 | `V1__init_schema.sql` | Table `clients` |
| Client | V2 | `V2__add_client_ref_sequence.sql` | Séquence CLT-XXXX |
| Client | V3 | `V3__add_trgm_search_index.sql` | Recherche trigram |
| Client | V4 | `V4__add_version_to_clients.sql` | Optimistic locking clients |
| Intervention | V1 | `V1__init_schema.sql` | Tables `interventions`, `intervention_items` |
| Intervention | V2 | `V2__add_photos_and_signatures.sql` | Photos + signatures |
| Intervention | V3 | `V3__add_intervention_sections.sql` | Horaires, résultat, facturation, sync |
| Intervention | V4 | `V4__add_client_id_index.sql` | Index client_id |
| Intervention | V5 | `V5__add_version_columns.sql` | Optimistic locking 3 tables |

### Schéma Supabase (alternative)

Tables: `users`, `clients`, `interventions`, `intervention_photos`, `intervention_items`, `equipment`  
RLS: Activé mais policies permissives (backend gère l'auth)

---

## 6. Sécurité

### Authentification

- **Keycloak** (port 8088) comme fournisseur OAuth2
- **Realm**: `ng-fields`
- **Clients**:
  - `ng-fields-backend` (confidential, client_credentials + service_accounts)
  - `ng-fields-web` (public, PKCE S256, redirect `localhost:4200`)
  - `ng-fields-mobile` (public, PKCE S256, redirect `127.0.0.1`)
- **JWT**: Claim `realm_access.roles` → mappé en `ROLE_*` Spring Security

### Rôles et permissions

| Rôle | Lectures | Écritures |
|------|----------|-----------|
| ADMIN | Toutes | Toutes (y compris admin users) |
| MANAGER | Toutes | Toutes sauf admin users |
| TECHNICIAN | Interventions assignées, clients | Interventions assignées uniquement |
| CLIENT_PORTAL | Profil, interventions client | Profil uniquement |

### Flux d'authentification

```
Client → Gateway → [JWT] → Service → Keycloak (validation JWT)
                  ↓
            Rôles extraits du JWT → @PreAuthorize
```

### Sécurité des endpoints

| Service | Pattern | Sécurité |
|---------|---------|----------|
| Gateway | `/api/public/**` | Public |
| Gateway | `/actuator/health`, `/actuator/info` | Public |
| Gateway | Tout le reste | JWT requis |
| Auth | `/api/public/register` | Public |
| Auth | `/api/admin/users/**` | ADMIN uniquement |
| Auth | `/api/users/me` | Tout utilisateur authentifié |
| Client | `/api/clients/**` | ADMIN/MANAGER/TECHNICIAN |
| Intervention | `/api/interventions/**` | ADMIN/MANAGER/TECHNICIAN |
| Intervention | PATCH endpoints | Propriétaire* |
| Media | `/api/media/**` | ADMIN/MANAGER/TECHNICIAN |
| Notification | `/api/notifications/**` | ADMIN/MANAGER/TECHNICIAN |
| Report | `/api/reports/**` | ADMIN/MANAGER uniquement |

### Configuration CORS

Origines autorisées: `http://localhost:4200` (Angular), `http://localhost:8100` (Mobile)

### Politique de mots de passe Keycloak

- Longueur min: 8
- 1 majuscule minimum
- 1 chiffre minimum
- 1 caractère spécial minimum
- Pas de mot de passe = username

### Brute force

- Activé: 5 échecs → lockout permanent
- Wait increment: 1800s

### Durées de vie des tokens

| Type | Durée |
|------|-------|
| Access token | 900s (15 min) |
| SSO session idle | 86400s (24h) |
| SSO session max | 604800s (7j) |
| Offline session | 2592000s (30j) |

---

## 7. Infrastructure & Déploiement

### Docker Compose

**Services principaux**:

| Service | Image | Port | Purpose |
|---------|-------|------|---------|
| `postgres` | `postgres:16-alpine` | 5432 (5433 en dev) | Base de données |
| `redis` | `redis:7-alpine` | 6379 | Cache / rate limiting |
| `keycloak` | `quay.io/keycloak/keycloak:26.0.9` | 8088 | IAM / SSO |
| `api` | `ng-fields-api:latest` | 8081 | Spring Boot API (placeholder) |

**Dépendances**:
- keycloak attend postgres healthy
- api attend postgres + redis healthy + keycloak started

**Variables d'environnement**:

| Variable | Default |
|----------|---------|
| `POSTGRES_DB` | `ng_fields` |
| `POSTGRES_USER` | `ng_fields_user` |
| `POSTGRES_PASSWORD` | `ngfields-dev` |
| `KC_ADMIN_PASSWORD` | `admin123` |
| `KEYCLOAK_AUTH_SERVER_URL` | `http://keycloak:8080` |
| `KEYCLOAK_REALM` | `ng-fields` |
| `KEYCLOAK_ISSUER_URI` | `http://keycloak:8080/realms/ng-fields` |

### Variables d'environnement Backend

| Variable | Default |
|----------|---------|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `5432` |
| `DB_NAME` | `ng_fields` |
| `DB_USER` | `ng_fields_user` |
| `DB_PASSWORD` | `change_me` |
| `KEYCLOAK_AUTH_SERVER_URL` | `http://localhost:8088` |
| `KEYCLOAK_ADMIN_CLIENT_ID` | `ng-fields-backend` |
| `KEYCLOAK_REALM` | `ng-fields` |
| `KEYCLOAK_ISSUER_URI` | `http://localhost:8088/realms/ng-fields` |
| `SMTP_HOST` | `localhost` |
| `SMTP_PORT` | `1025` |
| `MEDIA_UPLOAD_DIR` | `./uploads` |

### Ordre de démarrage

1. Gateway (8080)
2. Auth (8081)
3. Client (8082)
4. Intervention (8083)
5. Media (8084)

### Ports

| Service | Port | Profil |
|---------|------|--------|
| Gateway | 8080 | — |
| Auth | 8081 | — |
| Client | 8082 | — |
| Intervention | 8083 | — |
| Media | 8084 | — |
| Notification | 8085 | — |
| Report | 8086 | — |
| Keycloak | 8088 | Externe |
| PostgreSQL | 5432 | Externe |
| Redis | 6379 | Externe |

### Swagger

URL: `http://localhost:8080/swagger-ui.html`

---

## 8. Intégrations externes

### Keycloak

- **Version**: 26.0.9 (Docker) / 26.6.4 (local)
- **Realm**: `ng-fields`
- **Features**: token-exchange, admin-fine-grained-authz, health, metrics
- **OTP**: TOTP, HmacSHA1, 6 digits, 30s period

### OpenProject

- **API**: REST v3
- **Auth**: API key via Basic Auth ou Bearer header
- **Endpoints clés**:
  - `POST /api/v3/work_packages` — Créer ticket
  - `POST /api/v3/work_packages/form` — Valider puis commit
  - `PATCH /api/v3/work_packages/{id}` — Modifier
  - `GET /api/v3/statuses` / `/types` / `/priorities` — Lookup IDs dynamiquement
- **Important**: Les IDs (projects, types, statuses) dépendent de l'instance cible

### Twilio WhatsApp / Meta Cloud API

- **Usage**: Envoi PDF rapport via WhatsApp
- **Config**: `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_WHATSAPP_FROM`
- **Endpoint prévu**: `POST /api/notifications/whatsapp`
- **Note**: Technologies.md référence "Meta Cloud API" — les deux options sont prévues

### Sentry

- **Usage**: Monitoring (free tier)

---

## 9. Tests

### Couverture actuelle

| Service | Tests | Méthode | Statut |
|---------|-------|---------|--------|
| Auth | UserServiceTest (11) + AuditServiceTest (1) | Mockito | ✅ |
| Client | ClientServiceTest (10) | Mockito | ✅ |
| Gateway | KeycloakJwtAuthenticationConverterTest (4) | JUnit | ✅ |
| Intervention | InterventionServiceTest (12) | Mockito | ✅ |
| Notification | contextLoads (1) | Spring Boot | ✅ |
| Report | contextLoads (1) | Spring Boot | ✅ |
| **Total** | **40 tests** | — | **0 échec** |

### Ce qui reste à tester

- Tests d'intégration avec base de données (Testcontainers)
- Tests bout-en-bout via le gateway
- Tests de sécurité (rôles, permissions)

---

## 10. Décisions techniques

| Décision | Choix | Justification |
|----------|-------|---------------|
| Communication inter-services | RestClient | Pas de Feign (complexité inutile pour 5 services) |
| IDs | UUID | Scalable, pas de séquence, merge-friendly |
| Timestamps | OffsetDateTime | Timezone-aware, standard ISO 8601 |
| DTOs | Java records | Immutables, concis, `@Valid` natif |
| Synchro Keycloak/DB | Manuelle dans UserService | Pattern "write-through" |
| PDF | OpenPDF (LibrePDF) | Léger, pas de dépendance lourde |
| Migrations | Flyway | Versionné, réversible |
| Tests | JUnit 5 + Mockito | Standard Spring Boot |
| Auth | Keycloak over Supabase Auth | Découplage, compatible multi-projet |
| Backend | Spring Boot over Node.js | Stack Java existante |
| Mobile | Offline-first | Couverture réseau aléatoire en terrain |
| ORM mobile | Drift/SQLite | Seule solution Flutter mature avec migrations typées |
| Frontend | Angular standalone | Moins de boilerplate |
| Tests frontend | Vitest | 10-50x plus rapide que Jasmine/Karma |

### Patterns de sécurité transversaux

- Optimistic locking (`@Version`) sur toutes les entités
- Audit trail pour toutes les opérations utilisateur
- Recherche trigram pour les clients
- Protection CSV injection (CWE-1236)
- Transaction synchronization pour cleanup en cas de rollback
- Génération PDF/CSV
- Boucles de retry manuelles avec backoff exponentiel
- Protection path traversal

---

## 11. Roadmap & Prochaines étapes

### Roadmap

| Version | Dates | Focus | Story Points |
|---------|-------|-------|--------------|
| **V0** | 1-19 Juin | API Pure (auth, core, media, sync) | ~75 |
| **V0.1** | 22 Juin - 3 Juillet | Angular Web (dashboard + portal) | ~45 |
| **V1** | 6-17 Juillet | Flutter Mobile + notifications | ~50 |
| **Total** | 7 semaines | 7 sprints, ~29 US | ~170 SP |

### Prochaines étapes

1. **Notification service** — Implémentation complète (email, SMS, in-app)
2. **Report service** — Génération CSV/PDF améliorée
3. **Tests d'intégration** — Testcontainers pour tests avec vraie DB
4. **Circuit breaker** — Configurer Resilience4j sur les routes gateway
5. **Média** — Migrer vers Supabase Storage / MinIO S3
6. **Séparation DB** — Utilisateurs PostgreSQL distincts par service
7. **Documentation API** — Swagger agrégé via gateway

### Statut d'avancement

| Service | Statut | Remarques |
|---------|--------|-----------|
| Gateway | ✅ Fonctionnel | Rate limiting actif, manque circuit breaker |
| Auth | ✅ Fonctionnel | Keycloak synchronisé, audit trail |
| Client | ✅ Fonctionnel | CRUD complet, recherche, soft delete |
| Intervention | ✅ Fonctionnel | Tous endpoints métier implémentés |
| Media | ✅ Fonctionnel | Stockage fichier, upload/download/suppression |
| Notification | ⏳ Squelette | Mail + Thymeleaf prêts dans pom.xml |
| Report | ⏳ Squelette | CSV export implémenté |
| Tests | ✅ 40 tests | Auth, Client, Gateway, Intervention |

---

## 12. Annexes

### A. Structure des répertoires Backend

```
Backend/
├── pom.xml                          # Parent POM (multi-module)
├── .env                             # Variables d'environnement
├── .env.template                    # Template variables
├── ARCHITECTURE.md                  # Doc architecture
├── Backend.md                       # Notes backend
├── BACKEND_0.1.md                   # Documentation v0.1
├── Backend Audit 1 - 4000.md        # Audit findings
├── Backend Audit 4000 - 6000.md     # Audit findings
│
├── shared-lib/                      # Bibliothèque partagée
│   ├── pom.xml
│   └── src/main/java/tg/ngstars/common/security/
│       └── RealmRoleConverter.java
│
├── gateway-service/                 # API Gateway (port 8080)
│   ├── pom.xml
│   └── src/main/java/tg/ngstars/gateway/
│       ├── GatewayServiceApplication.java
│       └── config/
│           ├── KeycloakJwtAuthenticationConverter.java
│           ├── RateLimitConfig.java
│           └── SecurityConfig.java
│
├── auth-service/                    # Auth & Users (port 8081)
│   ├── pom.xml
│   └── src/main/java/tg/ngstars/auth/
│       ├── AuthServiceApplication.java
│       ├── config/
│       │   ├── GlobalExceptionHandler.java
│       │   ├── KeycloakAdminConfig.java
│       │   ├── KeycloakProperties.java
│       │   └── SecurityConfig.java
│       ├── controller/
│       │   └── UserController.java
│       ├── dto/
│       │   ├── CreateUserRequest.java
│       │   ├── RoleAssignRequest.java
│       │   ├── UpdateProfileRequest.java
│       │   ├── UpdateUserRequest.java
│       │   ├── UserResponse.java
│       │   └── UserStatusRequest.java
│       ├── exception/
│       │   ├── ConflictException.java
│       │   └── NotFoundException.java
│       ├── model/
│       │   ├── AuditLog.java
│       │   └── User.java
│       ├── repository/
│       │   ├── AuditLogRepository.java
│       │   └── UserRepository.java
│       └── service/
│           ├── AuditService.java
│           ├── SecurityUtils.java
│           └── UserService.java
│
├── client-service/                  # Clients (port 8082)
│   ├── pom.xml
│   └── src/main/java/tg/ngstars/client/
│       ├── ClientServiceApplication.java
│       ├── config/
│       │   ├── GlobalExceptionHandler.java
│       │   └── SecurityConfig.java
│       ├── controller/
│       │   └── ClientController.java
│       ├── dto/
│       │   ├── ClientResponse.java
│       │   ├── CreateClientRequest.java
│       │   └── UpdateClientRequest.java
│       ├── exception/
│       │   ├── ConflictException.java
│       │   └── NotFoundException.java
│       ├── model/
│       │   └── Client.java
│       ├── repository/
│       │   └── ClientRepository.java
│       └── service/
│           ├── ClientService.java
│           └── ReferenceGeneratorService.java
│
├── intervention-service/            # Interventions (port 8083)
│   ├── pom.xml
│   └── src/main/java/tg/ngstars/interv/
│       ├── InterventionServiceApplication.java
│       ├── client/
│       │   └── MediaClient.java
│       ├── config/
│       │   ├── GlobalExceptionHandler.java
│       │   ├── MediaClientConfig.java
│       │   └── SecurityConfig.java
│       ├── controller/
│       │   ├── InterventionController.java
│       │   ├── PhotoController.java
│       │   ├── SignatureController.java
│       │   └── SyncController.java
│       ├── dto/
│       │   ├── CreateInterventionRequest.java
│       │   ├── InterventionResponse.java
│       │   ├── ItemRequest.java
│       │   ├── ItemResponse.java
│       │   ├── PhotoResponse.java
│       │   ├── SignatureRequest.java
│       │   ├── SyncRequest.java
│       │   ├── UpdateBillingRequest.java
│       │   ├── UpdateDiagnosisRequest.java
│       │   ├── UpdateEquipmentRequest.java
│       │   ├── UpdateRecommendationsRequest.java
│       │   ├── UpdateResultRequest.java
│       │   └── UpdateScheduleRequest.java
│       ├── exception/
│       │   ├── ForbiddenException.java
│       │   ├── MediaServiceException.java
│       │   └── NotFoundException.java
│       ├── model/
│       │   ├── Intervention.java
│       │   ├── InterventionItem.java
│       │   ├── InterventionPhoto.java
│       │   └── PhotoType.java
│       ├── repository/
│       │   ├── InterventionPhotoRepository.java
│       │   └── InterventionRepository.java
│       └── service/
│           ├── InterventionService.java
│           ├── InterventionStatusService.java
│           ├── PdfService.java
│           ├── PhotoService.java
│           ├── SecurityUtils.java
│           └── SignatureService.java
│
├── media-service/                   # Fichiers (port 8084)
│   ├── pom.xml
│   └── src/main/java/tg/ngstars/media/
│       ├── MediaServiceApplication.java
│       ├── config/
│       │   ├── GlobalExceptionHandler.java
│       │   ├── MediaProperties.java
│       │   └── SecurityConfig.java
│       ├── controller/
│       │   └── FileController.java
│       └── service/
│           └── FileService.java
│
├── notification-service/            # Emails (port 8085)
│   ├── pom.xml
│   └── src/main/java/tg/ngstars/notification/
│       ├── NotificationServiceApplication.java
│       ├── config/
│       │   ├── GlobalExceptionHandler.java
│       │   └── SecurityConfig.java
│       ├── controller/
│       │   └── NotificationController.java
│       ├── dto/
│       │   └── EmailRequest.java
│       └── service/
│           └── EmailService.java
│
└── report-service/                  # Rapports (port 8086)
    ├── pom.xml
    └── src/main/java/tg/ngstars/report/
        ├── ReportServiceApplication.java
        ├── client/
        │   └── InterventionClient.java
        ├── config/
        │   ├── GlobalExceptionHandler.java
        │   └── SecurityConfig.java
        ├── controller/
        │   └── ReportController.java
        ├── dto/
        │   └── InterventionReportDto.java
        └── service/
            └── ReportService.java
```

### B. Commandes utiles

```bash
# Build complet
./mvnw clean install

# Lancer un service
./mvnw spring-boot:run -pl gateway-service
./mvnw spring-boot:run -pl auth-service
./mvnw spring-boot:run -pl client-service
./mvnw spring-boot:run -pl intervention-service
./mvnw spring-boot:run -pl media-service
./mvnw spring-boot:run -pl notification-service
./mvnw spring-boot:run -pl report-service

# Tests
./mvnw test

# Docker
docker-compose up -d
```

### C. Postman

- Collection: `doc/docs/tests/postman-collection.json` (32 requêtes)
- Environnement: `doc/docs/tests/postman-environment.json`

### D. Conventions de codage

- DTOs: Java records
- Injection: Constructeur (pas `@Autowired`)
- Timestamps: `@PrePersist` / `@PreUpdate`
- IDs: UUID
- Optimistic locking: `@Version`
- Audit: Toute opération utilisateur auditable
- Git: Branches `feature/US-*`, commits en français

---

## 13. Audit d'architecture

> **Source**: `Doc/Audit/NG-Fields_Backend_Audit_Enrichissement_Analyse.md`

### 13.1 Points forts

| Domaine | Observation | Impact |
|---------|-------------|--------|
| **Microservices** | 8 services + shared-lib, bien délimités | Scalabilité, maintenabilité |
| **API Gateway** | Spring Cloud Gateway (WebFlux), rate limiting | Point d'entrée unique, protection |
| **Sécurité** | Keycloak (OAuth2/JWT), RBAC multi-niveaux | Audit, conformité |
| **BD** | Schémas séparés par service, isolation | Évite couplage DB |
| **DTOs** | Records Java systématiques | Typage, immutabilité |
| **IDs** | UUID + localId pour idempotence | Offline-first, sync robuste |

### 13.2 Risques identifiés

#### Risques CRITIQUES

| # | Risque | Problème | Impact | Recommandation |
|---|--------|----------|--------|----------------|
| R1 | Monitoring & Observabilité | Sentry configuré, pas de centralized logging, pas de métriques Prometheus, pas de tracing distribué | Impossible d'identifier bottlenecks, root cause analysis impossible | Ajouter `spring-boot-starter-actuator`, `micrometer-core`, `sentry-spring-boot-starter` |
| R2 | Gestion d'erreurs inconsistante | 5 fichiers `GlobalExceptionHandler` différents, pas de standard de réponse | Frontend doit gérer 5 formats d'erreur | Créer `StandardErrorResponse` dans shared-lib |
| R3 | Tests insuffisants | 40 tests pour ~95 fichiers Java (42% couverture max), pas de test intégration | Regressions difficilement détectées, refactoring risqué | Couverture cible: 80% core business |
| R4 | Logging hétérogène | Chaque service log différemment, pas de correlation IDs | Tracing requête impossible entre services | Créer `LoggingInterceptor` avec X-Trace-ID |
| R5 | CI/CD incomplet | GitHub Actions mentionné mais workflows absents | Déploiements manuels, reproductibilité faible | Implémenter GitHub Actions build + test |

#### Risques MOYENS

| # | Risque | Problème | Solution |
|---|--------|----------|----------|
| R6 | Transactions distribuées | Pas de compensation pattern pour saga | Choreography-based Saga avec event sourcing |
| R7 | Sécurité rate limiting | Rate limiter par username, clé Redis pas nettoyée | Configurer replenish-rate et burst-capacity |
| R8 | Validation de données | DTOs avec peu de `@Valid`, pas de custom validators | Ajouter validators métier |

#### Risques MINEURS

| # | Risque | Solution |
|---|--------|----------|
| R9 | Discordance versions doc vs code | Mettre à jour doc avec version réelle |
| R10 | Pas de `.env.example` à la racine Backend | Créer fichier unique pour toute la stack |

### 13.3 Scores d'architecture

| Critère | Score | Justification |
|---------|-------|----------------|
| **Modularité** | 8/10 | Bonne séparation, mais shared-lib minimal |
| **Observabilité** | 2/10 | Quasi-absent, à rebuilder |
| **Testabilité** | 5/10 | 40 tests = commençant |
| **Sécurité** | 8/10 | Keycloak solide, rate limit OK |
| **Résilience** | 4/10 | Pas de retry, circuit breaker, saga |
| **Performance** | 6/10 | WebFlux OK, mais pas de tuning BD |
| **Maintenabilité** | 6/10 | DTOs bien, mais logging split |
| **Déploiement** | 3/10 | CI/CD absent |
| **Documentation** | 7/10 | Complète mais fragmentée |
| **Global** | **5.6/10** | À maturer — Passer de POC à production |

---

## 14. Analyse d'avancement par service

### 14.1 Matrice de maturité

| Service | Statut | Complétude | Fichiers | Tests | Priorisation |
|---------|--------|-----------|----------|-------|--------------|
| **gateway-service** | ✅ Prod | 95% | 4 Java + 3 config | 1 test | Stable |
| **auth-service** | ✅ Prod | 90% | 21 Java + 3 SQL | 2 tests | Stable |
| **client-service** | ✅ Prod | 85% | 13 Java + 4 SQL | 1 test | Stable |
| **intervention-service** | 🟡 Alpha | 75% | 37 Java + 5 SQL | 1 test | URGENT |
| **media-service** | 🟡 Alpha | 60% | 6 Java | 0 tests | URGENT |
| **notification-service** | 🔵 Skeleton | 20% | 6 Java | 1 test | À faire |
| **report-service** | 🔵 Skeleton | 25% | 7 Java | 1 test | À faire |
| **shared-lib** | 🟡 Minimal | 40% | 1 Java | 0 tests | URGENT |

### 14.2 État détaillé par service

#### gateway-service (95% - Stable)
- ✅ Routage multi-service, Security filters, Rate limiting (Redis + Resilience4j), JWT validation
- ⚠️ Manque: Health check endpoints, Swagger aggregation
- **Effort restant**: 3 jours

#### auth-service (90% - Production)
- ✅ User CRUD (Keycloak Admin API), Audit logging, Role management, Profile updates
- ⚠️ Manque: Password reset, 2FA, User activation email
- **Effort restant**: 5 jours

#### client-service (85% - Production)
- ✅ Client CRUD complet, Reference generation (NG-XXXX-XXXX), Contact management, Pagination/search
- ⚠️ Manque: Contacts table separate, validation avancée, duplication checks
- **Effort restant**: 4 jours

#### intervention-service (75% - Alpha)
- ✅ Intervention CRUD, Planification, Photos, Signatures, PDF generation
- ⚠️ Manque: Synchronisation offline complète, Historique versioning, Status workflow validation
- **Effort restant**: 7 jours
- **Fichiers critiques**: SyncController (80% vide), InterventionStatusService (à créer)

#### media-service (60% - Alpha)
- ✅ Upload/Download fichiers, File validation
- ⚠️ Manque: Anti-virus scanning, Image optimization, Versioning, Cleanup politique
- **Effort restant**: 6 jours

#### notification-service (20% - Skeleton)
- ✅ EmailRequest DTO, pom.xml avec Spring Mail + Thymeleaf
- ❌ EmailService vide, aucune template email, aucune intégration queue
- **Effort restant**: 8 jours

#### report-service (25% - Skeleton)
- ✅ ReportController stub, ReportService stub (CSV export)
- ❌ Aucune query complexe, aucune génération PDF
- **Effort restant**: 6 jours

#### shared-lib (40% - À étendre)
- ✅ RealmRoleConverter
- ❌ Pas d'entité commune, DTO base, exception standard, helper audit/logging
- **Effort restant**: 5 jours

### 14.3 Indicateurs de santé

| Service | Score /10 | Commentaire |
|---------|-----------|-------------|
| gateway-service | 8/10 | routing ✅, monitoring ❌ |
| auth-service | 7/10 | CRUD ✅, 2FA ❌ |
| client-service | 7/10 | basic ✅, contacts split ❌ |
| intervention-service | 5/10 | CRUD ✅, sync ❌ |
| media-service | 4/10 | upload ✅, security ❌ |
| notification-service | 1/10 | skeleton only |
| report-service | 2/10 | stub only |
| shared-lib | 3/10 | minimal |
| **Score global** | **4.6/10** | Cible Phase 3: 8.5/10 |

---

## 15. Plan d'enrichissement & Roadmap priorisée

### 15.1 Phase 1 — URGENT (2 semaines)
**Objectif**: Stabiliser architecture, passer alpha → beta

| # | Service | Tâche | Jours |
|---|---------|-------|-------|
| 1 | shared-lib | Créer base commune (StandardErrorResponse, AuditableEntity, LoggingConfig) | 5 |
| 2 | intervention-service | Compléter SyncController (localId → uuid mapping) | 7 |
| 3 | media-service | Sécurité + cleanup + tests | 6 |
| 4 | gateway-service | Health checks + trace headers (X-Trace-ID) | 3 |
| 5 | Tests | Ajouter 30 tests (80% coverage) | 8 |
| 6 | CI/CD | GitHub Actions (build + test) | 3 |

**Résultat**: 5 services production-ready

### 15.2 Phase 2 — IMPORTANT (3 semaines)
**Objectif**: Complétude services + observabilité

| # | Service | Tâche | Jours |
|---|---------|-------|-------|
| 1 | notification-service | EmailService complet + templates Thymeleaf + retry | 8 |
| 2 | report-service | Queries analytics + PDF + scheduled reports | 6 |
| 3 | auth-service | Password reset + 2FA + email verification | 5 |
| 4 | Monitoring | Sentry + logging centralisé | 4 |
| 5 | Documentation | Docs critique (Setup, Security, DB) | 5 |

**Résultat**: 8/8 services complets, observabilité

### 15.3 Phase 3 — AMÉLIORATION (1-2 mois)
- Resilience (circuit breaker, retry, saga)
- Performance (caching, indexing BD)
- Advanced security (2FA, OAuth2 PKCE)
- Mobile sync offline complet
- Kubernetes deployment
- Load testing

### 15.4 Blockers actuels

| Blocker | Sévérité | Impact | Solution |
|---------|----------|--------|----------|
| Notification-service non-implémentée | 🔴 Critical | Clients pas notifiés | Phase 2 (priorité 1) |
| SyncController 80% vide | 🔴 Critical | Offline sync impossible | Phase 1 (priorité 1) |
| Monitoring absent | 🔴 Critical | Pas d'observabilité prod | Phase 2 (priorité 1) |
| Tests minimes | 🔴 Critical | Refactoring risqué | Phase 1 (priorité 2) |
| Docs fragmentées | 🟡 High | Onboarding long | Phase 2 (priorité 2) |
| Media-service basique | 🟡 High | Pas de versioning | Phase 1 (priorité 2) |

---

## 16. Recommandations synthétiques

### 16.1 Quick wins (1-2 jours)

1. Créer `.env.example` centralisé
2. Ajouter Sentry à tous les `pom.xml`
3. Activer actuator endpoints
4. Créer fichier `02-SETUP-LOCAL.md`
5. Ajouter Correlation ID filter

### 16.2 Priorité absolue (2 semaines)

- Terminer SyncController (intervention offline)
- Créer shared-lib de base (errors, audit, logging)
- Ajouter 30+ tests (80% coverage)
- Implémenter CI/CD GitHub Actions
- Documenter `06-SECURITY.md`, `07-DATABASE.md`

### 16.3 Avant production (3-4 semaines)

- Notification-service (mail + SMS)
- Report-service (analytics + PDF)
- Monitoring (Sentry, logs centralisés)
- Media-service (virus scan, cleanup)
- Audit trail (JPA listeners)

### 16.4 Résumé exécutif

| Aspect | Aujourd'hui | Cible (3 sem.) | Effort |
|--------|-------------|----------------|--------|
| Services prod-ready | 3/8 (37%) | 8/8 (100%) | Phase 1-2 |
| Couverture tests | ~42% | 80%+ | Phase 1 |
| Observabilité | 0% | 100% | Phase 2 |
| Documentation | 40% | 95% | Phase 1-2 |
| CI/CD | 0% | 100% | Phase 1 |
| Sécurité | 80% | 95% | Phase 2 |

**→ Score architecture: 4.6/10 → 8.5/10 (+3.9 pts)**

---

> **Document généré le**: Juillet 2026  
> **Prochaine mise à jour**: Après implémentation notification-service et report-service complets
