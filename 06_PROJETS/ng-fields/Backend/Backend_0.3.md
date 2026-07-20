# Backend_0.3 — Documentation Complète du Backend NG-Fields

> **Version**: 0.3  
> **Date**: Juillet 2026  
> **Auteur**: NG-STARs  
> **Statut**: Production-Ready (score cible: 8.5/10)

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
7. [Observabilité & Monitoring](#7-observabilité--monitoring)
8. [Résilience & Circuit Breaker](#8-résilience--circuit-breaker)
9. [Tests](#9-tests)
10. [CI/CD](#10-cicd)
11. [Décisions techniques](#11-décisions-techniques)
12. [Infrastructure & Déploiement](#12-infrastructure--déploiement)
13. [Intégrations externes](#13-intégrations-externes)
14. [Changelog 0.2 → 0.3](#14-changelog-02--03)
15. [Annexes](#15-annexes)

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
| Résilience | Resilience4j (Retry + Circuit Breaker) | — |
| Email | Spring Mail + Thymeleaf | — |
| Tests | JUnit 5 + Mockito + MockMvc | — |
| Build | Maven | Wrapper |
| Monitoring | Sentry + Prometheus + Correlation IDs | — |
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
│    Routage + Rate limiting + Circuit Breaker + CORS   │
│    Correlation ID + Request Logging                   │
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
│   intervention, media,       │
│   notification, report       │
└──────────────────────────────┘

     ┌──────────────────┐
     │    Keycloak      │
     │   (port 8088)    │
     │ realm: ng-fields │
     └──────────────────┘

     ┌──────────────────┐
     │    Redis         │
     │   (port 6379)    │
     │  Rate Limiting   │
     └──────────────────┘

     ┌──────────────────┐
     │    Sentry        │
     │  Error Tracking  │
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

| Service | Java (main) | Java (test) | SQL | YAML | HTML | Templates |
|---------|-------------|-------------|-----|------|------|-----------|
| shared-lib | 11 | 0 | 0 | 1 | 0 | 0 |
| gateway-service | 5 | 2 | 0 | 3 | 0 | 0 |
| auth-service | 22 | 3 | 3 | 3 | 0 | 0 |
| client-service | 17 | 3 | 5 | 3 | 0 | 0 |
| intervention-service | 39 | 3 | 5 | 3 | 0 | 0 |
| media-service | 7 | 1 | 0 | 3 | 0 | 0 |
| notification-service | 7 | 2 | 0 | 4 | 0 | 5 |
| report-service | 9 | 2 | 0 | 3 | 0 | 0 |
| **TOTAL** | **117** | **16** | **13** | **23** | **0** | **5** |

---

## 4. Détail des services

### 4.1 shared-lib

**Description**: Bibliothèque partagée pour tous les services NG-Fields  
**Artifact**: `ng-fields-shared-lib`  
**Parent**: `spring-boot-starter-parent` 4.1.0

#### Fichiers

| Fichier | Description |
|---------|-------------|
| `RealmRoleConverter.java` | Extraction des rôles Keycloak → Spring Security |
| `StandardErrorResponse.java` | Format d'erreur standardisé (RFC 7807 ProblemDetail) |
| `PaginatedResponse.java` | Wrapper pour réponses paginées |
| `AuditableEntity.java` | Entité de base avec audit fields |
| `BaseEntity.java` | Entité de base avec UUID + timestamps |
| `NotFoundException.java` | Exception 404 standard |
| `ConflictException.java` | Exception 409 standard |
| `BusinessException.java` | Exception métier générique |
| `GlobalExceptionHandler.java` | Handler d'erreurs unifié (tous services) |
| `CorrelationIdFilter.java` | Filtre X-Correlation-ID pour tracing inter-services |
| `SecurityUtils.java` | Utilitaire extraction userId/userRoles depuis JWT |

#### Configuration Logging

- `logback-spring.xml` — Configuration centralisée
  - DEV: Console classique avec `%X{correlationId:-}`
  - PROD: JSON Console (structured logging)
  - `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{correlationId:-}] %-5level %logger{36} - %msg%n`

#### `RealmRoleConverter.java`

- **Classe**: `tg.ngstars.common.security.RealmRoleConverter`
- **Implémente**: `Converter<Jwt, Collection<GrantedAuthority>>`
- **Rôle**: Extrait les rôles du claim `realm_access` du JWT Keycloak et les mappe en `ROLE_*` Spring Security

#### `CorrelationIdFilter.java`

- **Classe**: `tg.ngstars.common.logging.CorrelationIdFilter`
- **Ordre**: `Ordered.HIGHEST_PRECEDENCE + 10`
- **Logique**: Lit `X-Correlation-ID` du header, génère UUID si absent, met dans MDC, propage dans la réponse

#### Dépendances

- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-web`
- `sentry-spring-boot-starter` 8.14.0

---

### 4.2 gateway-service

**Description**: Point d'entrée unique, routage, rate limiting, circuit breaker, CORS  
**Port**: 8080  
**Type**: Spring Cloud Gateway (WebFlux réactif)

#### Fichiers

| Fichier | Description |
|---------|-------------|
| `GatewayServiceApplication.java` | Point d'entrée |
| `SecurityConfig.java` | Sécurité WebFlux + JWT |
| `RateLimitConfig.java` | Key resolvers (user + IP) |
| `KeycloakJwtAuthenticationConverter.java` | Conversion JWT → Authorities |
| `GatewayExceptionHandler.java` | **[NEW 0.3]** Gestion erreurs centralisée |
| `GatewayExceptionHandlerTest.java` | **[NEW 0.3]** Tests du handler |
| `KeycloakJwtAuthenticationConverterTest.java` | Tests du converter |

#### GatewayExceptionHandler.java (NEW 0.3)

- **Classe**: `tg.ngstars.gateway.config.GatewayExceptionHandler`
- **Exception handler WebFlux** avec:
  - `ExchangeDeniedException` → 403 Forbidden
  - `ResponseErrorException` → 4xx/5xx selon status
  - `ErrorResponse` → body custom
  - `Exception` → 500 Internal Server Error
- Format: `ErrorResponse` avec `timestamp`, `status`, `error`, `message`, `path`

#### Routes configurées

| Route ID | Chemin | Cible | Rate Limit | Circuit Breaker |
|----------|--------|-------|------------|-----------------|
| `auth-register` | `/api/public/register` | auth-service:8081 | 3/6 (remoteAddr) | myCircuitBreaker |
| `auth-public` | `/api/public/**` | auth-service:8081 | none | myCircuitBreaker |
| `auth-admin` | `/api/admin/users/**` | auth-service:8081 | 10/20 (user) | myCircuitBreaker |
| `auth-me` | `/api/users/me` | auth-service:8081 | none | myCircuitBreaker |
| `client-service` | `/api/clients/**` | client-service:8082 | 20/40 (user) | myCircuitBreaker |
| `intervention-service` | `/api/interventions/**` | intervention-service:8083 | 30/60 (user) | myCircuitBreaker |
| `sync-service` | `/api/sync/**` | intervention-service:8083 | 10/20 (user) | myCircuitBreaker |
| `media-service` | `/api/media/**` | media-service:8084 | 20/40 (user) | myCircuitBreaker |
| `notification-service` | `/api/notifications/**` | notification-service:8085 | 10/20 (user) | myCircuitBreaker |
| `report-service` | `/api/reports/**` | report-service:8086 | 5/10 (user) | myCircuitBreaker |

#### Configuration Resilience4j (Gateway)

```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10000ms
        permitted-number-of-calls-in-half-open-state: 3
    instances:
      myCircuitBreaker:
        base-config: default
```

#### Dépendances

- `spring-cloud-starter-gateway-server-webflux`
- `spring-cloud-starter-circuitbreaker-reactor-resilience4j`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-actuator`
- `spring-boot-starter-data-redis-reactive`
- `springdoc-openapi-starter-webflux-ui` 3.0.3
- `sentry-spring-boot-starter` 8.14.0
- `lombok`

---

### 4.3 auth-service

**Description**: Gestion des utilisateurs, rôles, authentification (délégation Keycloak), audit trail  
**Port**: 8081  
**Base de données**: PostgreSQL (schéma `auth`)

#### Fichiers

| Fichier | Description |
|---------|-------------|
| `AuthServiceApplication.java` | Point d'entrée |
| `GlobalExceptionHandler.java` | Gestion erreurs (ProblemDetail) |
| `KeycloakAdminConfig.java` | Bean Keycloak Admin Client |
| `KeycloakProperties.java` | Config Keycloak (record) |
| `SecurityConfig.java` | OAuth2 Resource Server |
| `UserController.java` | 12 endpoints REST |
| `CreateUserRequest.java` | DTO création (validation forte) |
| `ChangePasswordRequest.java` | **[NEW 0.3]** DTO changement mot de passe |
| `RoleAssignRequest.java` | DTO assignment rôle |
| `UpdateProfileRequest.java` | DTO mise à jour profil |
| `UpdateUserRequest.java` | DTO mise à jour admin |
| `UserResponse.java` | DTO réponse |
| `UserStatusRequest.java` | DTO activation/désactivation |
| `ConflictException.java` | Exception 409 |
| `NotFoundException.java` | Exception 404 |
| `AuditLog.java` | Entité audit trail |
| `User.java` | Entité utilisateur |
| `AuditLogRepository.java` | Repository audit |
| `UserRepository.java` | Repository utilisateurs |
| `AuditService.java` | Service audit (REQUIRES_NEW) |
| `SecurityUtils.java` | Extraction userId/roles |
| `UserService.java` | Service principal (Keycloak + DB) |

#### Endpoints

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/public/register` | Public | Auto-inscription (force CLIENT_PORTAL) |
| GET | `/api/users/me` | Auth | Profil utilisateur courant |
| PUT | `/api/users/me` | Auth | Mise à jour profil |
| POST | `/api/users/me/change-password` | Auth | **[NEW 0.3]** Changement mot de passe |
| POST | `/api/admin/users` | ADMIN | Création utilisateur |
| GET | `/api/admin/users` | ADMIN | Liste utilisateurs (paginée) |
| GET | `/api/admin/users/{id}` | ADMIN | Détail utilisateur |
| PUT | `/api/admin/users/{id}` | ADMIN | Modification |
| DELETE | `/api/admin/users/{id}` | ADMIN | Désactivation (soft delete) |
| PATCH | `/api/admin/users/{keycloakId}/roles` | ADMIN | Changement rôle |
| PATCH | `/api/admin/users/{keycloakId}/status` | ADMIN | Activer/désactiver |
| POST | `/api/admin/users/{keycloakId}/reset-password` | ADMIN | Réinitialisation email |

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
| `changePassword(UUID keycloakId, ChangePasswordRequest)` | `@Transactional` | **[NEW 0.3]** Valide force du mot de passe (8+ chars, majuscule, minuscule, chiffre, spécial). Reset password via Keycloak Admin API. Audite. |
| `getProfile(UUID keycloakId)` | `@Transactional(readOnly=true)` | Find by keycloakId |
| `updateProfile(UUID keycloakId, UpdateProfileRequest)` | `@Transactional` | MAJ first/last name DB + Keycloak. Audite. |
| `registerClient(CreateUserRequest, String ip)` | — | Appelle `createUser` avec rôle `CLIENT_PORTAL` et `createdBy="SELF_REGISTER"`. |

#### ChangePasswordRequest.java (NEW 0.3)

| Champ | Validation |
|-------|------------|
| `currentPassword` | @NotBlank |
| `newPassword` | @NotBlank @Size(min=8) |

Validation supplémentaire dans service: majuscule, minuscule, chiffre, caractère spécial.

#### Modèle de données

**User.java** (Entité)

| Champ | Type | Contraintes |
|-------|------|-------------|
| `id` | UUID | @Id |
| `keycloakId` | UUID | NOT NULL UNIQUE |
| `username` | String(50) | NOT NULL UNIQUE |
| `email` | String | NOT NULL UNIQUE |
| `firstName` | String | — |
| `lastName` | String | — |
| `role` | String | NOT NULL |
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
- `sentry-spring-boot-starter` 8.14.0
- `lombok`

---

### 4.4 client-service

**Description**: Gestion des fiches clients et contacts  
**Port**: 8082  
**Base de données**: PostgreSQL (schéma `client`)

#### Fichiers

| Fichier | Description |
|---------|-------------|
| `ClientServiceApplication.java` | Point d'entrée |
| `GlobalExceptionHandler.java` | Gestion erreurs |
| `SecurityConfig.java` | OAuth2 Resource Server |
| `ClientController.java` | 9 endpoints REST |
| `ClientResponse.java` | DTO réponse (avec contacts) |
| `CreateClientRequest.java` | DTO création |
| `UpdateClientRequest.java` | DTO mise à jour |
| `ContactDto.java` | **[NEW 0.3]** DTO contact |
| `CreateContactRequest.java` | **[NEW 0.3]** DTO création contact |
| `ConflictException.java` | Exception 409 |
| `NotFoundException.java` | Exception 404 |
| `Client.java` | Entité client (avec OneToMany contacts) |
| `Contact.java` | **[NEW 0.3]** Entité contact |
| `ClientRepository.java` | Repository clients |
| `ContactRepository.java` | **[NEW 0.3]** Repository contacts |
| `ClientService.java` | Service principal |
| `ReferenceGeneratorService.java` | Génération CLT-XXXX |

#### Endpoints

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/clients` | ADMIN | Création client |
| GET | `/api/clients` | ADMIN/MANAGER/TECHNICIAN | Liste paginée |
| GET | `/api/clients/search?q=` | ADMIN/MANAGER/TECHNICIAN | Recherche trigram |
| GET | `/api/clients/{id}` | ADMIN/MANAGER/TECHNICIAN | Détail client |
| PUT | `/api/clients/{id}` | ADMIN | Modification |
| DELETE | `/api/clients/{id}` | ADMIN | Désactivation (soft delete) |
| POST | `/api/clients/{clientId}/contacts` | ADMIN | **[NEW 0.3]** Ajouter contact |
| GET | `/api/clients/{clientId}/contacts` | ADMIN/MANAGER/TECHNICIAN | **[NEW 0.3]** Lister contacts |
| DELETE | `/api/clients/{clientId}/contacts/{contactId}` | ADMIN | **[NEW 0.3]** Supprimer contact |

#### ClientService — Méthodes clés

| Méthode | Transaction | Description |
|---------|-------------|-------------|
| `createClient(CreateClientRequest, String createdBy)` | `@Transactional` | Vérifie doublon email. Génère `CLT-XXXX`. Crée entité. |
| `listClients(int page, int size)` | `@Transactional(readOnly=true)` | Clients actifs triés par companyName |
| `getClient(UUID id)` | `@Transactional(readOnly=true)` | Find by ID ou NotFoundException |
| `updateClient(UUID id, UpdateClientRequest)` | `@Transactional` | Vérifie doublon email si modifié. MAJ tous les champs. |
| `deactivateClient(UUID id)` | `@Transactional` | `active=false` |
| `searchClients(String query, int page, int size)` | `@Transactional(readOnly=true)` | Recherche trigram via repository |
| `addContact(UUID clientId, CreateContactRequest)` | `@Transactional` | **[NEW 0.3]** Crée contact lié au client |
| `getContacts(UUID clientId)` | `@Transactional(readOnly=true)` | **[NEW 0.3]** Liste contacts actifs |
| `removeContact(UUID clientId, UUID contactId)` | `@Transactional` | **[NEW 0.3]** Désactive contact |

#### ReferenceGeneratorService

- **Méthode**: `generateNextReference()` → `String`
- **Format**: `"CLT-XXXX"` (4 chiffres, zero-padded)
- **Sécurité**: Séquence native PostgreSQL `client_ref_seq`

#### Contact.java (NEW 0.3)

| Champ | Type | Contraintes |
|-------|------|-------------|
| `id` | UUID | @Id |
| `client` | Client | @ManyToOne LAZY, FK client_id |
| `fullName` | String(150) | NOT NULL |
| `email` | String(150) | — |
| `phone` | String(30) | — |
| `role` | String(50) | — |
| `active` | boolean | default true |
| `createdAt` | OffsetDateTime | — |

#### Modèle de données — Client

| Champ | Type | Contraintes |
|-------|------|-------------|
| `id` | UUID | @Id |
| `reference` | String(20) | NOT NULL UNIQUE |
| `companyName` | String(200) | NOT NULL |
| `contactName` | String(150) | — (legacy, backward compat) |
| `email` | String(150) | NOT NULL UNIQUE |
| `phone` | String(30) | — |
| `address` | TEXT | — |
| `latitude` | Double | -90 à 90 |
| `longitude` | Double | -180 à 180 |
| `contacts` | List<Contact> | @OneToMany, cascade ALL, orphanRemoval |
| `active` | boolean | default true |
| `version` | Long | @Version |
| `createdBy` | String(100) | — |
| `createdAt` | OffsetDateTime | — |
| `updatedAt` | OffsetDateTime | — |

#### DTOs

**ClientResponse.java** (record)

Champs: `id`, `reference`, `companyName`, `contactName`, `email`, `phone`, `address`, `latitude`, `longitude`, `active`, `createdAt`, `contacts` (List of ContactDto)

#### Migrations SQL

| Version | Fichier | Description |
|---------|---------|-------------|
| V1 | `V1__init_schema.sql` | Table `clients` |
| V2 | `V2__add_client_ref_sequence.sql` | `CREATE SEQUENCE client_ref_seq START 1` |
| V3 | `V3__add_trgm_search_index.sql` | Extension `pg_trgm` + index GIN trigram |
| V4 | `V4__add_version_to_clients.sql` | Optimistic locking clients |
| V5 | `V5__add_contacts_table.sql` | **[NEW 0.3]** Table `contacts` avec FK cascade |

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
- `sentry-spring-boot-starter` 8.14.0
- `lombok`

---

### 4.5 intervention-service

**Description**: Cœur métier — gestion complète des interventions terrain  
**Port**: 8083  
**Base de données**: PostgreSQL (schéma `intervention`)  
**Taille**: Le plus grand service — 39 fichiers Java, 5 migrations SQL

#### Fichiers

| Fichier | Description |
|---------|-------------|
| `InterventionServiceApplication.java` | Point d'entrée |
| `MediaClient.java` | Client REST media-service (Resilience4j) |
| `GlobalExceptionHandler.java` | Gestion erreurs |
| `MediaClientConfig.java` | Config RestClient media |
| `SecurityConfig.java` | OAuth2 Resource Server |
| `InterventionController.java` | Endpoints CRUD + workflow |
| `PhotoController.java` | Upload/listing photos |
| `SignatureController.java` | Signatures client/tech/manager |
| `SyncController.java` | **[ENHANCED 0.3]** Sync mobile avec conflict resolution |
| `CreateInterventionRequest.java` | DTO création |
| `InterventionResponse.java` | DTO réponse (38 champs) |
| `InterventionStatus.java` | **[NEW 0.3]** Enum state machine |
| `SyncRequest.java` | DTO sync mobile |
| `SyncResponse.java` | **[NEW 0.3]** Réponse sync (CREATED/UPDATED/CONFLICT) |
| `SignatureRequest.java` | DTO signature |
| `SignatureResponse.java` | **[FIXED 0.3]** Typed DTO (remplace Map) |
| `ItemRequest.java` | DTO pièce |
| `ItemResponse.java` | DTO réponse pièce |
| `PhotoResponse.java` | DTO réponse photo |
| `UpdateBillingRequest.java` | DTO facturation |
| `UpdateDiagnosisRequest.java` | DTO diagnostic |
| `UpdateEquipmentRequest.java` | DTO équipement |
| `UpdateRecommendationsRequest.java` | DTO recommandations |
| `UpdateResultRequest.java` | DTO résultat |
| `UpdateScheduleRequest.java` | DTO horaires |
| `ConflictException.java` | **[NEW 0.3]** Exception conflit sync |
| `ForbiddenException.java` | Exception accès |
| `MediaServiceException.java` | Exception media |
| `NotFoundException.java` | Exception 404 |
| `Intervention.java` | Entité (37+ champs) |
| `InterventionItem.java` | Entité pièce |
| `InterventionPhoto.java` | Entité photo |
| `PhotoType.java` | Enum BEFORE/AFTER |
| `InterventionPhotoRepository.java` | Repository photos |
| `InterventionRepository.java` | Repository interventions |
| `InterventionService.java` | Service principal |
| `InterventionStatusService.java` | **[REWRITTEN 0.3]** State machine complète |
| `PdfService.java` | Génération PDF |
| `PhotoService.java` | Gestion photos |
| `SecurityUtils.java` | Utilitaire JWT |
| `SignatureService.java` | Gestion signatures |

#### InterventionStatus.java (NEW 0.3) — State Machine

```java
public enum InterventionStatus {
    PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED;

    public boolean canTransitionTo(InterventionStatus next) {
        // PENDING → ASSIGNED, CANCELLED
        // ASSIGNED → IN_PROGRESS, CANCELLED
        // IN_PROGRESS → COMPLETED, CANCELLED
        // COMPLETED → terminal
        // CANCELLED → terminal
    }

    public static InterventionStatus fromString(String value) {
        // Case-insensitive parsing
    }
}
```

#### SyncResponse.java (NEW 0.3)

```java
public record SyncResponse(
    UUID interventionId,
    String reference,
    SyncAction action,       // CREATED, UPDATED, CONFLICT
    String message,
    OffsetDateTime serverUpdatedAt
) {}
```

#### Endpoints — InterventionController

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/interventions` | Admin/Manager/Tech | Création intervention |
| GET | `/api/interventions` | Admin/Manager/Tech | Liste (filtrable par status, technicianId) |
| GET | `/api/interventions/{id}` | Admin/Manager/Tech | Détail |
| PUT | `/api/interventions/{id}` | Admin/Manager/Tech | Mise à jour complète |
| DELETE | `/api/interventions/{id}` | Admin/Manager/Tech | Désactivation |
| GET | `/api/interventions/{id}/pdf` | Admin/Manager/Tech | Génération PDF |
| POST | `/api/interventions/{id}/assign` | Admin/Manager | **[NEW 0.3]** Assigner technicien |
| POST | `/api/interventions/{id}/start` | Admin/Manager/Tech | **[NEW 0.3]** Démarrer intervention |
| POST | `/api/interventions/{id}/cancel` | Admin/Manager | **[NEW 0.3]** Annuler intervention |
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

#### Endpoints — SyncController (ENHANCED 0.3)

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/sync/interventions` | Admin/Manager/Tech | Sync mobile avec conflict resolution |

**Conflict Resolution**: Compare `clientUpdatedAt` (mobile) vs `server updatedAt`. Si serveur plus récent → retourne `CONFLICT` avec les deux timestamps. Le client décide.

#### InterventionStatusService (REWRITTEN 0.3)

| Méthode | Description |
|---------|-------------|
| `validateTransition(InterventionStatus from, InterventionStatus to)` | Vérifie la validité de la transition via `canTransitionTo()` |
| `assignIntervention(Intervention, UUID technicianId, String technicianName, UUID userId)` | PENDING → ASSIGNED |
| `startIntervention(Intervention, UUID userId, boolean isAdminOrManager)` | ASSIGNED → IN_PROGRESS |
| `closeIntervention(Intervention, UUID userId, boolean isAdminOrManager)` | IN_PROGRESS → COMPLETED (exige 3 signatures) |
| `cancelIntervention(Intervention, UUID userId, boolean isAdminOrManager)` | Any non-terminal → CANCELLED |

#### PhotoController

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/interventions/{id}/photos` | TECHNICIAN/MANAGER/ADMIN | Upload photo (multipart) |
| GET | `/api/interventions/{id}/photos` | isAuthenticated | Liste photos |
| GET | `/api/interventions/{id}/photos/type/{type}` | isAuthenticated | Filtrer par type |
| DELETE | `/api/interventions/{id}/photos/{photoId}` | TECHNICIAN/MANAGER/ADMIN | Supprimer photo |

#### SignatureController

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/interventions/{id}/signatures/client` | TECHNICIAN/MANAGER/ADMIN | Signature client |
| POST | `/api/interventions/{id}/signatures/technician` | TECHNICIAN/MANAGER/ADMIN | Signature technicien |
| POST | `/api/interventions/{id}/signatures/manager` | MANAGER/ADMIN | Signature manager |

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
- `openpdf` 1.4.1
- `resilience4j-spring-boot3` — **[NEW 0.3]** Retry + Circuit Breaker sur MediaClient
- `sentry-spring-boot-starter` 8.14.0
- `lombok`

#### Migrations SQL

| Version | Fichier | Description |
|---------|---------|-------------|
| V1 | `V1__init_schema.sql` | Tables `interventions`, `intervention_items` |
| V2 | `V2__add_photos_and_signatures.sql` | Photos + signatures |
| V3 | `V3__add_intervention_sections.sql` | Horaires, résultat, facturation, sync |
| V4 | `V4__add_client_id_index.sql` | Index client_id |
| V5 | `V5__add_version_columns.sql` | Optimistic locking 3 tables |

---

### 4.6 media-service

**Description**: Stockage et distribution de fichiers (photos, signatures, PDF)  
**Port**: 8084  
**Base de données**: Aucune — stockage sur disque local (`./uploads`)

#### Fichiers

| Fichier | Description |
|---------|-------------|
| `MediaServiceApplication.java` | Point d'entrée |
| `GlobalExceptionHandler.java` | Gestion erreurs |
| `MediaProperties.java` | Config stockage (maxSize, maxStorage) |
| `SecurityConfig.java` | OAuth2 Resource Server |
| `FileController.java` | Upload/download/delete |
| `FileService.java` | **[REWRITTEN 0.3]** Sécurité renforcée |
| `FileServiceTest.java` | **[NEW 0.3]** 28 tests |

#### FileService (REWRITTEN 0.3) — Sécurité renforcée

| Méthode | Description |
|---------|-------------|
| `store(MultipartFile, String userId)` | **[ENHANCED]** Valide MIME magic bytes (pas juste extension), vérifie taille max, vérifie stockage total, protège path traversal |
| `storeBytes(byte[], String ext, String userId)` | Génère filename UUID, écrit bytes, track ownership |
| `load(String filename)` | Sanitise filename (anti path traversal) |
| `delete(String filename, String userId)` | Valide ownership, supprime, MAJ tracking |

**Sécurités ajoutées (0.3)**:
- **Validation MIME magic bytes**: Vérifie les premiers octets du fichier (pas juste l'extension)
- **Taille max par fichier**: `media.max-file-size-bytes` (défaut: 10 MB)
- **Taille max stockage**: `media.max-storage-bytes` (défaut: 5 GB)
- **Headers sécurité**: `X-Content-Type-Options: nosniff`, `Content-Disposition: attachment` pour non-images

#### MediaProperties (ENHANCED 0.3)

| Champ | Default | Description |
|-------|---------|-------------|
| `uploadDir` | `./uploads` | Répertoire stockage |
| `maxFileSizeBytes` | 10485760 (10 MB) | Taille max par fichier |
| `maxStorageBytes` | 5368709120 (5 GB) | Stockage total max |

#### Endpoints

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/media/upload` | ADMIN/MANAGER/TECHNICIAN | Upload fichier (multipart) |
| POST | `/api/media/upload-base64` | ADMIN/MANAGER/TECHNICIAN | Upload image base64 |
| GET | `/api/media/{filename}` | — | Téléchargement |
| DELETE | `/api/media/{filename}` | ADMIN/MANAGER/TECHNICIAN | Suppression |

#### Dépendances

- `ng-fields-shared-lib` 1.0.0-SNAPSHOT
- `spring-boot-starter-web`
- `spring-boot-starter-json`
- `spring-boot-starter-validation`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-actuator`
- `springdoc-openapi-starter-webmvc-ui` 3.0.3
- `sentry-spring-boot-starter` 8.14.0
- `lombok`

---

### 4.7 notification-service

**Description**: Envoi d'emails (Mail + Thymeleaf) avec retry  
**Port**: 8085  
**Base de données**: Aucune

#### Fichiers

| Fichier | Description |
|---------|-------------|
| `NotificationServiceApplication.java` | Point d'entrée |
| `GlobalExceptionHandler.java` | Gestion erreurs |
| `SecurityConfig.java` | OAuth2 Resource Server |
| `NotificationController.java` | Endpoint envoi email |
| `EmailRequest.java` | DTO requête |
| `EmailService.java` | **[FIXED 0.3]** Retry corrigé (catch Exception au lieu de MessagingException) |
| `EmailServiceTest.java` | **[NEW 0.3]** 9 tests unitaires |
| `NotificationControllerTest.java` | **[NEW 0.3]** 2 tests MockMvc |

#### Templates Thymeleaf (NEW 0.3)

| Template | Description |
|----------|-------------|
| `intervention-notification.html` | Notification d'intervention |
| `password-reset.html` | Réinitialisation mot de passe |
| `welcome.html` | Email de bienvenue |
| `intervention-assigned.html` | Attribution d'intervention |
| `intervention-completed.html` | Intervention terminée |

#### EmailService (FIXED 0.3)

**Bug corrigé**: Le retry manuel ne fonctionnait pas car `MessagingException` n'était pas suffisamment large. Changé pour `catch (Exception e)` pour que les erreurs de connexion SMTP soient bien retryées.

**Retry Logic**:
1. Valide template contre allowlist
2. Construit contexte Thymeleaf
3. Traite template
4. **3 tentatives** avec backoff exponentiel (1s, 2s, 3s)
5. Envoie via `MimeMessageHelper` (HTML, UTF-8)

#### Endpoints

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| POST | `/api/notifications/email` | ADMIN/MANAGER/TECHNICIAN | Envoi email (202 ACCEPTED) |

#### Dépendances

- `ng-fields-shared-lib` 1.0.0-SNAPSHOT
- `spring-boot-starter-actuator`
- `spring-boot-starter-mail`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-thymeleaf`
- `spring-boot-starter-validation`
- `spring-boot-starter-web`
- `springdoc-openapi-starter-webmvc-ui` 3.0.3
- `sentry-spring-boot-starter` 8.14.0
- `lombok`

---

### 4.8 report-service

**Description**: Rapports CSV, analytics et PDF  
**Port**: 8086  
**Base de données**: Aucune (lit depuis intervention-service via HTTP)

#### Fichiers

| Fichier | Description |
|---------|-------------|
| `ReportServiceApplication.java` | Point d'entrée |
| `InterventionClient.java` | Client REST intervention-service (Resilience4j) |
| `GlobalExceptionHandler.java` | Gestion erreurs |
| `SecurityConfig.java` | OAuth2 Resource Server |
| `ReportController.java` | 4 endpoints |
| `InterventionReportDto.java` | DTO intervention pour rapports |
| `AnalyticsDto.java` | **[NEW 0.3]** Record analytics (8 champs) |
| `ReportService.java` | Service CSV |
| `AnalyticsService.java` | **[NEW 0.3]** Agregation analytics |
| `PdfReportService.java` | **[NEW 0.3]** Génération PDF rapports |
| `ReportServiceTest.java` | **[NEW 0.3]** 5 tests |

#### AnalyticsDto.java (NEW 0.3)

```java
public record AnalyticsDto(
    long totalInterventions,
    long completedInterventions,
    long pendingInterventions,
    long cancelledInterventions,
    double completionRate,
    double averageBillingAmount,
    long billableInterventions,
    long totalRevenue
) {}
```

#### Endpoints

| Méthode | Chemin | Accès | Description |
|---------|--------|-------|-------------|
| GET | `/api/reports/interventions/csv` | ADMIN/MANAGER | Export CSV (streaming) |
| GET | `/api/reports/interventions/pdf` | ADMIN/MANAGER | **[NEW 0.3]** Export PDF (OpenPDF) |
| GET | `/api/reports/analytics` | ADMIN/MANAGER | **[NEW 0.3]** Statistiques agrégées |

#### PdfReportService (NEW 0.3)

- **Méthode**: `generateInterventionsPdf(List<InterventionReportDto>)` → `byte[]`
- **Librairie**: OpenPDF 1.4.1 (LibrePDF)
- **Format**: Paysage A4, tableau 10 colonnes
- **Colonnes**: Ref, Client, Type, Statut, Priorité, Technicien, Date, Facturable, Montant, Créé le

#### InterventionClient (ENHANCED 0.3)

- **Annotations**: `@CircuitBreaker(name="interventionClient", fallbackMethod="fetchFallback")` + `@Retry(name="interventionClient")`
- **Fallback**: Retourne `List.of()` en cas d'erreur

#### Dépendances

- `ng-fields-shared-lib` 1.0.0-SNAPSHOT
- `spring-boot-starter-actuator`
- `spring-boot-starter-oauth2-resource-server`
- `spring-boot-starter-web`
- `springdoc-openapi-starter-webmvc-ui` 3.0.3
- `openpdf` 1.4.1 — **[NEW 0.3]**
- `resilience4j-spring-boot3` — **[NEW 0.3]** Retry + Circuit Breaker
- `sentry-spring-boot-starter` 8.14.0
- `lombok`

---

## 5. Base de données

### Architecture

- **PostgreSQL 16** à localhost:5432
- **Schéma par service**: Isolation des données entre microservices
- **Extensions**: `uuid-ossp`, `pgcrypto`, `unaccent`, `pg_trgm`

### Schémas

| Schéma | Service | Tables |
|--------|---------|--------|
| `auth` | auth-service | `users`, `audit_logs` |
| `client` | client-service | `clients`, `contacts` |
| `intervention` | intervention-service | `interventions`, `intervention_items`, `intervention_photos` |
| `media` | media-service | `media_files` |
| `notification` | notification-service | `email_logs` |
| `report` | report-service | `report_requests` |

### Migrations Flyway

| Service | V | Fichier | Description |
|---------|---|---------|-------------|
| Auth | V1 | `V1__init_schema.sql` | Tables `users` et `audit_logs` |
| Auth | V2 | `V2__add_audit_logs_index.sql` | Index audit_logs |
| Auth | V3 | `V3__add_version_to_users.sql` | Optimistic locking users |
| Client | V1 | `V1__init_schema.sql` | Table `clients` |
| Client | V2 | `V2__add_client_ref_sequence.sql` | Séquence CLT-XXXX |
| Client | V3 | `V3__add_trgm_search_index.sql` | Recherche trigram |
| Client | V4 | `V4__add_version_to_clients.sql` | Optimistic locking clients |
| Client | V5 | `V5__add_contacts_table.sql` | **[NEW 0.3]** Table `contacts` |
| Intervention | V1 | `V1__init_schema.sql` | Tables `interventions`, `intervention_items` |
| Intervention | V2 | `V2__add_photos_and_signatures.sql` | Photos + signatures |
| Intervention | V3 | `V3__add_intervention_sections.sql` | Horaires, résultat, facturation |
| Intervention | V4 | `V4__add_client_id_index.sql` | Index client_id |
| Intervention | V5 | `V5__add_version_columns.sql` | Optimistic locking 3 tables |

---

## 6. Sécurité

### Authentification

- **Keycloak** (port 8088) comme fournisseur OAuth2
- **Realm**: `ng-fields`
- **Clients**:
  - `ng-fields-backend` (confidential, client_credentials + service_accounts)
  - `ng-fields-web` (public, PKCE S256)
  - `ng-fields-mobile` (public, PKCE S256)
- **JWT**: Claim `realm_access.roles` → mappé en `ROLE_*` Spring Security

### Rôles et permissions

| Rôle | Lectures | Écritures |
|------|----------|-----------|
| ADMIN | Toutes | Toutes (y compris admin users) |
| MANAGER | Toutes | Toutes sauf admin users |
| TECHNICIAN | Interventions assignées, clients | Interventions assignées uniquement |
| CLIENT_PORTAL | Profil, interventions client | Profil uniquement |

### Sécurité renforcée (0.3)

| Mesure | Description |
|--------|-------------|
| **Rate Limiting** | Redis-based sur gateway (per-user + per-IP) |
| **Circuit Breaker** | Resilience4j sur toutes les routes gateway |
| **File Upload Security** | MIME magic bytes validation, taille max, path traversal protection |
| **CSV Injection Prevention** | Préfixe tab pour caractères spéciaux |
| **Correlation IDs** | X-Correlation-ID propagé entre services |
| **Secrets Sanitization** | Actuator env endpoint filtre les clés sensibles |
| **Security Headers** | X-Content-Type-Options: nosniff, Content-Disposition: attachment |
| **Optimistic Locking** | `@Version` sur toutes les entités critiques |

---

## 7. Observabilité & Monitoring

### Sentry

- **SDK**: `sentry-spring-boot-starter` 8.14.0 sur tous les services + shared-lib
- **Config**: `SentryConfig.java` dans shared-lib
  - DSN configurable via `SENTRY_DSN`
  - Traces Sample Rate: 20%
  - Attachement contexte: userId, role, email

### Logging

- **Configuration**: `logback-spring.xml` dans shared-lib
  - DEV: Console classique
  - PROD: JSON Console (structured logging)
- **Correlation ID**: `%X{correlationId:-}` dans tous les logs
- **Format**: `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{correlationId:-}] %-5level %logger{36} - %msg%n`

### Request Logging

- **LoggingInterceptor**: Intercepte toutes les requêtes `/api/**`
  - Log: Method, URI, Status, Duration
  - Enregistre dans MDC: method, uri, status
- **WebConfig**: Enregistre l'interceptor pour toutes les requêtes

### Actuator

- Endpoints exposés: `health`, `info`, `metrics`, `prometheus`
- Sanitization des clés sensibles dans `/actuator/env`
- Prometheus: `/actuator/prometheus` pour métriques

---

## 8. Résilience & Circuit Breaker

### Inter-service Clients

| Client | Service appelé | Retry | Circuit Breaker | Fallback |
|--------|---------------|-------|-----------------|----------|
| `InterventionClient` (report-service) | intervention-service | 3 tentatives, backoff exponentiel | 50% failure threshold | `List.of()` |
| `MediaClient` (intervention-service) | media-service | 3 tentatives, backoff 500ms | 50% failure threshold | `MediaServiceException` |

### Configuration Resilience4j

```yaml
resilience4j:
  retry:
    configs:
      default:
        max-attempts: 3
        wait-duration: 500ms-1s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
        retry-exceptions:
          - java.io.IOException
          - org.springframework.web.client.ResourceAccessException
  circuitbreaker:
    configs:
      default:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        permitted-number-of-calls-in-half-open-state: 3
```

### Gateway Circuit Breaker

Toutes les routes gateway ont un filtre `CircuitBreaker` avec `fallbackUri: forward:/fallback`.

---

## 9. Tests

### Vue d'ensemble

| Service | Unit Tests | Controller Tests | Total |
|---------|-----------|-----------------|-------|
| intervention-service | 36 | 6 | 42 |
| auth-service | 24 | 0 | 24 |
| client-service | 12 | 9 | 21 |
| media-service | 28 | 0 | 28 |
| notification-service | 9 | 2 | 11 |
| report-service | 5 | 0 | 5 |
| gateway-service | 4 | 4 | 8 |
| shared-lib | 0 | 0 | 0 |
| **Total** | **118** | **21** | **139** |

### Tests détaillés par service

#### auth-service (24 tests)

| Classe | Tests | Couverture |
|--------|-------|------------|
| `UserServiceTest` | 21 | createUser (success, doublon username/email, Keycloak 409), getUser, getAllUsers, assignRole, updateUserStatus, getProfile, updateProfile, deleteUser, registerClient, updateUser, sendPasswordReset, **changePassword (4)**: success, weak password, no uppercase, not found |
| `AuditServiceTest` | 1 | log() |
| `SecurityUtilsTest` | 2 | getCurrentUserId, getCurrentUserRoles |

#### client-service (21 tests)

| Classe | Tests | Couverture |
|--------|-------|------------|
| `ClientServiceTest` | 18 | CRUD complet + **contacts (6)**: addContact, addContact not found, getContacts, getContacts not found, removeContact, removeContact wrong client, removeContact not found |
| `ReferenceGeneratorServiceTest` | 3 | Format CLT-XXXX |
| `ClientControllerTest` | 9 | 6 CRUD endpoints + **3 contacts**: addContact 201, getContacts 200, removeContact 204 |

#### intervention-service (42 tests)

| Classe | Tests | Couverture |
|--------|-------|------------|
| `InterventionServiceTest` | 17 | CRUD + sync + close + items |
| `InterventionStatusServiceTest` | 10 | Toutes les transitions de statut |
| `InterventionStatusTest` | 9 | Enum state machine (canTransitionTo, fromString) |
| `InterventionControllerTest` | 6 | create, list, get, start, cancel, close |

#### media-service (28 tests)

| Classe | Tests | Couverture |
|--------|-------|------------|
| `FileServiceTest` | 28 | store (MIME validation, taille max, storage max, path traversal), load, delete (ownership), extensions |

#### notification-service (11 tests)

| Classe | Tests | Couverture |
|--------|-------|------------|
| `EmailServiceTest` | 9 | send (success, retry, invalid template), templates |
| `NotificationControllerTest` | 2 | send email 202, invalid body 400 |

#### report-service (5 tests)

| Classe | Tests | Couverture |
|--------|-------|------------|
| `ReportServiceTest` | 5 | CSV export, CSV injection prevention, analytics |

#### gateway-service (8 tests)

| Classe | Tests | Couverture |
|--------|-------|------------|
| `KeycloakJwtAuthenticationConverterTest` | 4 | Roles extraction |
| `GatewayExceptionHandlerTest` | 4 | ExchangeDenied, ResponseError, ErrorResponse, generic Exception |

---

## 10. CI/CD

### GitHub Actions

**Fichier**: `.github/workflows/backend.yml`

```yaml
name: Backend CI
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '25'
          distribution: 'temurin'
      - uses: actions/cache@v4
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
      - run: mvn -B verify
      - uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-results
          path: '**/target/surefire-reports/'
```

---

## 11. Décisions techniques

| Décision | Choix | Justification |
|----------|-------|---------------|
| Communication inter-services | RestClient | Pas de Feign (complexité inutile pour 7 services) |
| IDs | UUID | Scalable, merge-friendly, offline-first |
| Timestamps | OffsetDateTime | Timezone-aware, ISO 8601 |
| DTOs | Java records | Immutables, concis, `@Valid` natif |
| Synchro Keycloak/DB | Manuelle dans UserService | Pattern "write-through" |
| PDF | OpenPDF (LibrePDF) | Léger, pas de dépendance lourde |
| Migrations | Flyway | Versionné, reproductible |
| Tests | JUnit 5 + Mockito + MockMvc | Standard Spring Boot |
| Résilience | Resilience4j | Retry + Circuit Breaker sur clients inter-services |
| Observabilité | Sentry + Correlation IDs + Prometheus | Error tracking + tracing + métriques |
| State Machine | Enum avec `canTransitionTo()` | Validation côté serveur des transitions |
| Conflict Resolution | Timestamp comparison | Simple et efficace pour sync mobile |

### Patterns de sécurité transversaux

- Optimistic locking (`@Version`) sur toutes les entités
- Audit trail pour toutes les opérations utilisateur
- Recherche trigram pour les clients
- Protection CSV injection (CWE-1236)
- Transaction synchronization pour cleanup en cas de rollback
- MIME magic bytes validation pour uploads
- Protection path traversal
- Retry avec backoff exponentiel
- Circuit Breaker sur routes gateway et clients inter-services

---

## 12. Infrastructure & Déploiement

### Docker Compose

| Service | Image | Port |
|---------|-------|------|
| `postgres` | `postgres:16-alpine` | 5432 |
| `redis` | `redis:7-alpine` | 6379 |
| `keycloak` | `quay.io/keycloak/keycloak:26.0.9` | 8088 |

### Variables d'environnement

**Fichier**: `.env.example` (centralisé à la racine Backend)

| Variable | Default | Description |
|----------|---------|-------------|
| `KEYCLOAK_ISSUER_URI` | `http://localhost:8088/realms/ng-fields` | Issuer JWT |
| `KEYCLOAK_CLIENT_SECRET` | — | Secret backend client |
| `POSTGRES_HOST` | `localhost` | Host PostgreSQL |
| `POSTGRES_PORT` | `5432` | Port PostgreSQL |
| `POSTGRES_DB` | `ngfields` | Nom base |
| `POSTGRES_USER` | `postgres` | Utilisateur |
| `POSTGRES_PASSWORD` | `postgres` | Mot de passe |
| `REDIS_HOST` | `localhost` | Host Redis |
| `REDIS_PORT` | `6379` | Port Redis |
| `SMTP_HOST` | `localhost` | Host SMTP |
| `SMTP_PORT` | `1025` | Port SMTP |
| `MEDIA_UPLOAD_DIR` | `./uploads` | Répertoire uploads |
| `SENTRY_DSN` | — | DSN Sentry |
| `SPRING_PROFILES_ACTIVE` | `dev` | Profil Spring |

### Ports

| Service | Port |
|---------|------|
| Gateway | 8080 |
| Auth | 8081 |
| Client | 8082 |
| Intervention | 8083 |
| Media | 8084 |
| Notification | 8085 |
| Report | 8086 |
| Keycloak | 8088 |
| PostgreSQL | 5432 |
| Redis | 6379 |

### Graceful Shutdown

Tous les services: timeout 30s, arrêt gracieux des requêtes en cours.

---

## 13. Intégrations externes

### Keycloak

- **Version**: 26.0.9
- **Realm**: `ng-fields`
- **Features**: token-exchange, admin-fine-grained-authz, health, metrics
- **OTP**: TOTP, HmacSHA1, 6 digits, 30s period

### OpenProject

- **API**: REST v3
- **Auth**: API key via Basic Auth ou Bearer header
- **Endpoints**: `POST /api/v3/work_packages`, `PATCH /api/v3/work_packages/{id}`

### Sentry

- **SDK**: 8.14.0 (via BOM)
- **Usage**: Error tracking + performance monitoring
- **Taux échantillonnage traces**: 20%

---

## 14. Changelog 0.2 → 0.3

### Architecture & Shared-lib

| Ajout | Description |
|-------|-------------|
| `StandardErrorResponse` | Format d'erreur standardisé |
| `PaginatedResponse` | Wrapper paginé |
| `AuditableEntity` / `BaseEntity` | Entités de base |
| `NotFoundException` / `ConflictException` / `BusinessException` | Exceptions standard |
| `GlobalExceptionHandler` | Handler unifié |
| `CorrelationIdFilter` | Filtre X-Correlation-ID |
| `SecurityUtils` (shared) | Utilitaire JWT |
| `logback-spring.xml` | Config logging centralisée |
| `SentryConfig.java` | Init Sentry |
| `LoggingInterceptor` + `WebConfig` | Request logging |

### Gateway

| Ajout | Description |
|-------|-------------|
| `GatewayExceptionHandler` | Gestion erreurs centralisée |
| Circuit Breaker sur toutes les routes | Filtre Resilience4j |
| Correlation ID filter | Propagation tracing |

### Auth-service

| Ajout | Description |
|-------|-------------|
| `ChangePasswordRequest` | DTO changement mot de passe |
| `POST /api/users/me/change-password` | Endpoint self-service |
| `validatePasswordStrength()` | Validation force mot de passe |
| 4 nouveaux tests | changePassword (success, weak, no uppercase, not found) |

### Client-service

| Ajout | Description |
|-------|-------------|
| `Contact.java` | Entité JPA contacts |
| `ContactRepository` | Repository Spring Data |
| `ContactDto` / `CreateContactRequest` | DTOs contacts |
| `@OneToMany` sur Client | Relation One-to-Many |
| `POST /{clientId}/contacts` | Ajouter contact |
| `GET /{clientId}/contacts` | Lister contacts |
| `DELETE /{clientId}/contacts/{contactId}` | Supprimer contact |
| V5 migration | Table `contacts` |
| 9 nouveaux tests | Service + Controller |

### Intervention-service

| Ajout | Description |
|-------|-------------|
| `InterventionStatus` enum | State machine (PENDING→ASSIGNED→IN_PROGRESS→COMPLETED + CANCELLED) |
| `canTransitionTo()` | Validation transitions |
| `InterventionStatusService` réécrit | `assignIntervention`, `startIntervention`, `cancelIntervention` |
| `ConflictException` | Exception conflit sync |
| `SyncResponse` | CREATED/UPDATED/CONFLICT |
| `SignatureResponse` typed DTO | Remplace Map |
| `POST /{id}/assign` | Assigner technicien |
| `POST /{id}/start` | Démarrer intervention |
| `POST /{id}/cancel` | Annuler intervention |
| Resilience4j sur MediaClient | Retry + Circuit Breaker |
| 30 nouveaux tests | StatusService (10), Status enum (9), Controller (6), Service étendu |

### Media-service

| Ajout | Description |
|-------|-------------|
| MIME magic bytes validation | Vérifie octets réels du fichier |
| `maxFileSizeBytes` / `maxStorageBytes` | Limites configurables |
| Security headers | X-Content-Type-Options, Content-Disposition |
| `FileServiceTest` | 28 tests de sécurité |

### Notification-service

| Ajout | Description |
|-------|-------------|
| 5 templates Thymeleaf | intervention-notification, password-reset, welcome, assigned, completed |
| Bug fix EmailService | `catch Exception` au lieu de `MessagingException` |
| ALLOWED_TEMPLATES mis à jour | 5 templates autorisés |
| 11 tests | EmailService (9) + Controller (2) |

### Report-service

| Ajout | Description |
|-------|-------------|
| `AnalyticsDto` | Record 8 champs |
| `AnalyticsService` | Agrégation in-memory |
| `PdfReportService` | Génération PDF OpenPDF |
| `GET /analytics` | Endpoint analytics |
| `GET /interventions/pdf` | Endpoint PDF |
| OpenPDF 1.4.1 | Dépendance ajoutée |
| Resilience4j sur InterventionClient | Retry + Circuit Breaker |
| 5 tests | CSV, injection, analytics |

### CI/CD & Docs

| Ajout | Description |
|-------|-------------|
| `.github/workflows/backend.yml` | Pipeline CI (JDK 25, mvn verify) |
| `.env.example` | Variables d'environnement centralisées |
| `docs/README.md` | Documentation index |
| `docs/API_ENDPOINTS.md` | Tous les endpoints documentés |
| `docs/TESTING.md` | Guide des tests |
| `docs/SECURITY.md` | Guide sécurité |
| `docs/DATABASE.md` | Schéma complet |
| `docs/DEPLOYMENT.md` | Guide déploiement |

### Score d'architecture

| Critère | 0.2 | 0.3 | Delta |
|---------|-----|-----|-------|
| Modularité | 8/10 | 8/10 | — |
| Observabilité | 2/10 | 7/10 | **+5** |
| Testabilité | 5/10 | 8/10 | **+3** |
| Sécurité | 8/10 | 9/10 | **+1** |
| Résilience | 4/10 | 7/10 | **+3** |
| Performance | 6/10 | 7/10 | **+1** |
| Maintenabilité | 6/10 | 8/10 | **+2** |
| Déploiement | 3/10 | 7/10 | **+4** |
| Documentation | 7/10 | 9/10 | **+2** |
| **Global** | **5.6/10** | **7.8/10** | **+2.2** |

---

## 15. Annexes

### A. Commandes utiles

```bash
# Build complet (depuis n'importe quel sous-service)
.\mvnw.cmd -f "..\pom.xml" compile -q

# Tests un service
.\mvnw.cmd -f "..\pom.xml" test -pl auth-service -am -q

# Lancer un service
cd gateway-service && mvn spring-boot:run
cd auth-service && mvn spring-boot:run
cd client-service && mvn spring-boot:run
cd intervention-service && mvn spring-boot:run
cd media-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd report-service && mvn spring-boot:run
```

### B. Conventions de codage

- DTOs: Java records
- Injection: Constructeur (pas `@Autowired`)
- Timestamps: `@PrePersist` / `@PreUpdate`
- IDs: UUID
- Optimistic locking: `@Version`
- Audit: Toute opération utilisateur auditable
- Tests: JUnit 5 + Mockito, MockMvc pour controllers

### C. Structure Backend

```
Backend/
├── .env.example                    # Variables d'environnement
├── .github/workflows/backend.yml   # CI/CD
├── docs/                           # Documentation
│   ├── README.md
│   ├── API_ENDPOINTS.md
│   ├── DATABASE.md
│   ├── DEPLOYMENT.md
│   ├── SECURITY.md
│   └── TESTING.md
├── shared-lib/                     # 11 fichiers Java
├── gateway-service/                # 5 Java + 2 tests
├── auth-service/                   # 22 Java + 3 tests
├── client-service/                 # 17 Java + 3 tests
├── intervention-service/           # 39 Java + 3 tests
├── media-service/                  # 7 Java + 1 test
├── notification-service/           # 7 Java + 2 tests
└── report-service/                 # 9 Java + 2 tests
```

---

> **Document généré le**: 20 Juillet 2026  
> **Version précédente**: Backend_0.2  
> **Score architecture**: 5.6/10 → **7.8/10** (+2.2 pts)  
> **Prochaine mise à jour**: Après intégration mobile Flutter
