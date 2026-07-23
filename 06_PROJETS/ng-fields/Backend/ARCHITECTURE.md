# Architecture du Backend NG-Fields

## 1. Présentation générale

Le backend NG-Fields est une architecture **microservices** (Spring Boot 4.1.0 / Java 25) sécurisée par **Keycloak** (OAuth2/JWT), avec une **API Gateway** comme point d'entrée unique et une base de données **PostgreSQL** avec un schéma dédié par service.

### Stack technique

| Composant | Technologie | Version |
|---|---|---|
| Runtime | Java | 25 |
| Framework | Spring Boot | 4.1.0 |
| Gateway | Spring Cloud Gateway (WebFlux) | 2025.1.2 |
| Auth | Keycloak (OAuth2/JWT) | 26.6.4 |
| Base de données | PostgreSQL | 18 |
| ORM | JPA / Hibernate | `ddl-auto: update` |
| Migrations | Hibernate DDL | — |
| API Docs | SpringDoc OpenAPI | 3.0.3 |
| PDF | OpenPDF (LibrePDF) | 1.4.1 |
| Email | Resend API | — |
| Push | Firebase Admin SDK | 9.2.0 (conditional) |
| SSE | SseEmitter (Server-Sent Events) | — |
| Logs | Logback + logstash-logback-encoder | 8.0 |
| Rate limiting | Redis + Resilience4j | — |
| Tests | JUnit 5 + Mockito | 65 unit tests |

---

## 2. Architecture des services

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

### Accès direct aux services (dev uniquement)

Chaque service expose son propre port et son propre SecurityConfig. En développement, on peut appeler directement `localhost:8081`, `8082`, etc. En production, seul le port 8080 (gateway) doit être exposé.

---

## 3. Détail des services

### 3.1 API Gateway — Port 8080

**Rôle** : Point d'entrée unique, routage, rate limiting, CORS, agrégation Swagger, gestion d'erreurs centralisée.

**Routes configurées** :

| Route | Cible | Rate limité |
|---|---|---|
| `/api/public/**` → Auth (8081) | Inscription, health | Non |
| `/api/admin/users/**` → Auth (8081) | Admin users | Oui (10 req/s) |
| `/api/admin/companies/**` → Auth (8081) | Admin companies | Oui (10 req/s) |
| `/api/client/**` → Auth (8081) | Client management | Oui (10 req/s) |
| `/api/users/me` → Auth (8081) | Profil | Non |
| `/api/clients/**` → Client (8082) | Clients CRUD | Oui (20 req/s) |
| `/api/interventions/**` → Intervention (8083) | Interventions | Oui (30 req/s) |
| `/api/manager/**` → Intervention (8083) | Manager dashboard/planning | Oui (30 req/s) |
| `/api/sync/**` → Intervention (8083) | Sync mobile | Non |
| `/api/media/**` → Media (8084) | Fichiers | Non |
| `/api/notifications/**` → Notification (8085) | Notifications | Oui (10 req/s) |
| `/api/push/**` → Notification (8085) | Push notifications | Non |
| `/api/reports/**` → Report (8086) | Rapports | Oui (5 req/s) |
| `/api/report/**` → Report (8086) | Templates (PDF/Email) | Oui (5 req/s) |

**Rate limiting** : Redis + Resilience4j circuit breaker.

**Gestion d'erreurs** : `GlobalExceptionHandler` (WebFlux `ErrorWebExceptionHandler`) retourne des réponses RFC 7807 Problem Detail (`application/problem+json`) pour toutes les erreurs gateway (403, 4xx, 500).

### 3.2 Auth Service — Port 8081

**Rôle** : Gestion des utilisateurs, rôles, authentification (délégation Keycloak), audit trail, gestion des entreprises.

**Tables** : `users` (copie locale des utilisateurs Keycloak), `audit_logs`, `companies`, `company_users`, `company_access_log`.

**Email** : Resend API (via `EmailService`), templates Thymeleaf (intervention-notification, password-reset, welcome, intervention-assigned, intervention-completed).

**Endpoints** :

| Méthode | Chemin | Accès | Description |
|---|---|---|---|
| POST | `/api/public/register` | Public | Auto-inscription (force rôle CLIENT_USER) |
| GET | `/api/public/health` | Public | Health check |
| GET | `/api/users/me` | Auth | Profil utilisateur courant |
| PUT | `/api/users/me` | Auth | Mise à jour profil |
| POST | `/api/admin/users` | ADMIN | Création utilisateur |
| GET | `/api/admin/users` | ADMIN | Liste utilisateurs |
| GET | `/api/admin/users/{id}` | ADMIN | Détail utilisateur |
| PUT | `/api/admin/users/{id}` | ADMIN | Modification |
| DELETE | `/api/admin/users/{id}` | ADMIN | Désactivation |
| PATCH | `/api/admin/users/{keycloakId}/roles` | ADMIN | Changement rôle |
| PATCH | `/api/admin/users/{keycloakId}/status` | ADMIN | Activer/désactiver |
| POST | `/api/admin/users/{keycloakId}/reset-password` | ADMIN | Réinitialisation mot de passe |
| POST | `/api/admin/companies` | ADMIN/MANAGER | Inscription d'une entreprise |
| GET | `/api/admin/companies` | ADMIN/MANAGER | Liste des entreprises |
| GET | `/api/admin/companies/{id}` | ADMIN/MANAGER | Détail entreprise |
| PUT | `/api/admin/companies/{id}` | ADMIN | Modification entreprise |
| DELETE | `/api/admin/companies/{id}` | ADMIN | Soft delete entreprise |
| POST | `/api/client/users` | CLIENT_ADMIN | Création utilisateur dans l'entreprise |
| GET | `/api/client/users` | CLIENT_ADMIN | Liste utilisateurs de l'entreprise |
| PUT | `/api/client/users/{id}` | CLIENT_ADMIN | Modification utilisateur |
| DELETE | `/api/client/users/{id}` | CLIENT_ADMIN | Soft delete utilisateur |
| PUT | `/api/client/users/{id}/role` | CLIENT_ADMIN | Changement rôle |
| PUT | `/api/client/users/{id}/password` | CLIENT_ADMIN | Réinitialisation mot de passe |
| PUT | `/api/client/change-password` | CLIENT | Changement mot de passe (vérifie old password via Keycloak token endpoint) |
| GET | `/api/client/interventions` | CLIENT | Interventions de l'entreprise |
| GET | `/api/client/dashboard` | CLIENT_ADMIN | KPIs de l'entreprise |

**Rôles** : `ADMIN`, `MANAGER`, `TECHNICIAN`, `CLIENT_ADMIN`, `CLIENT_USER`, `CLIENT_VIEWER`.

**Particularité** : Toute opération utilisateur (création, modification, désactivation, changement rôle) est synchronisée entre Keycloak et la base locale, avec une trace d'audit.

### 3.3 Client Service — Port 8082

**Rôle** : Gestion des fiches clients.

**Table** : `clients`.

**Endpoints** :

| Méthode | Chemin | Accès | Description |
|---|---|---|---|
| POST | `/api/clients` | ADMIN | Création client |
| GET | `/api/clients` | ADMIN/MANAGER/TECHNICIAN | Liste paginée |
| GET | `/api/clients/search?q=` | ADMIN/MANAGER/TECHNICIAN | Recherche (nom, contact, email) |
| GET | `/api/clients/{id}` | ADMIN/MANAGER/TECHNICIAN | Détail client |
| PUT | `/api/clients/{id}` | ADMIN | Modification |
| DELETE | `/api/clients/{id}` | ADMIN | Désactivation (soft delete) |

### 3.4 Intervention Service — Port 8083

**Rôle** : Cœur métier — gestion complète des interventions terrain.

**Tables** : `interventions`, `intervention_items`, `intervention_photos`.

**Fonctionnalités avancées :**
- **SSE Real-time** : `SseEmitterManager` + `EventController` — push temps réel des événements `INTERVENTION_STATUS_CHANGED`, `NEW_TICKET`
- **Dashboard Manager** : `DashboardController` + `DashboardService` — KPIs, stats par technicien/client, export CSV/Excel/HTML
- **Planning Manager** : `ManagerController` — planning hebdomadaire, affectation interventions
- **Export** : `ExportService` — CSV/HTML avec filtres (status, technicianId), escaping injection
- **Batch Sync** : `SyncController` + `SyncService` — synchronisation hors-ligne, last-write-wins
- **Email** : `InterventionEmailService` — Resend API pour envoi rapports
- **GPS** : champs `gps_latitude`/`gps_longitude`, validation [-90,90], [-180,180]

**Endpoints** :

| Méthode | Chemin | Accès | Description |
|---|---|---|---|
| POST | `/api/interventions` | Admin/Manager/Technicien | Création intervention |
| GET | `/api/interventions` | Admin/Manager/Technicien | Liste (filtrable par `status`, `technicianId`) |
| GET | `/api/interventions/{id}` | Admin/Manager/Technicien | Détail |
| PUT | `/api/interventions/{id}` | Admin/Manager/Technicien | Mise à jour complète |
| DELETE | `/api/interventions/{id}` | Admin/Manager/Technicien | Désactivation |
| GET | `/api/interventions/{id}/pdf` | Admin/Manager/Technicien | Génération PDF |
| GET | `/api/interventions/stats` | MANAGER | Stats (total, active, pending, completed, month, today) |
| GET | `/api/interventions/stats/by-technician` | MANAGER | Stats par technicien |
| GET | `/api/interventions/stats/by-client` | MANAGER | Stats par client |
| PATCH | `/api/interventions/{id}/schedule` | Propriétaire* | Horaires |
| PATCH | `/api/interventions/{id}/equipment` | Propriétaire* | Équipement |
| PATCH | `/api/interventions/{id}/diagnosis` | Propriétaire* | Diagnostic |
| PATCH | `/api/interventions/{id}/result` | Propriétaire* | Résultat |
| PATCH | `/api/interventions/{id}/recommendations` | Propriétaire* | Recommandations |
| POST | `/api/interventions/{id}/send/email` | Propriétaire* | Envoi rapport par email |
| POST | `/api/interventions/{id}/items` | Propriétaire* | Ajout pièce |
| PUT | `/api/interventions/{id}/items/{itemId}` | Propriétaire* | Modif pièce |
| DELETE | `/api/interventions/{id}/items/{itemId}` | Propriétaire* | Suppr pièce |
| POST | `/api/interventions/{id}/close` | Propriétaire* | Clôture (auto si 3 signatures) |
| POST | `/api/interventions/{id}/assign` | MANAGER | Affectation technicien |
| GET | `/api/manager/interventions/weekly-schedule` | MANAGER | Planning hebdomadaire |
| POST | `/api/manager/interventions` | MANAGER | Création intervention planifiée |
| PUT | `/api/manager/interventions/{id}/assign` | MANAGER | Réaffectation |
| GET | `/api/manager/events` | MANAGER | SSE real-time events |
| GET | `/api/manager/export/csv` | MANAGER | Export CSV (filtres) |
| GET | `/api/manager/export/excel` | MANAGER | Export Excel (filtres) |
| GET | `/api/manager/export/html` | MANAGER | Export HTML (filtres) |
| POST | `/api/sync/batch` | Admin/Manager/Technicien | Batch sync (offline→online) |
| GET | `/api/client/interventions` | CLIENT | Interventions entreprise (filtré company_id) |
| GET | `/api/client/dashboard` | CLIENT_ADMIN | KPIs entreprise (filtré company_id) |

\* *Propriétaire = technicien assigné, sauf ADMIN/MANAGER qui peuvent modifier toute intervention.*

**Champs de l'entité Intervention** :

- Informations client (nom, email, téléphone, adresse)
- Équipement (type, marque, modèle, série, localisation)
- Diagnostic, travail effectué
- Statut (PENDING → COMPLETED)
- Horaires (départ, arrivée, début, fin, durée calculée)
- Résultat, recommandations
- ~~Facturation (facturable, montant, notes)~~ ❌ SUPPRIMÉ
- 3 signatures (client, technicien, responsable)
- Photos (avant/après, 5 max chaque)
- Pièces utilisées (type, description, quantité, prix unitaire)
- Synchronisation mobile (local_id)

### 3.5 Media Service — Port 8084

**Rôle** : Stockage et distribution de fichiers (photos, signatures, PDF).

**Particularité** : Service sans base de données — stockage sur disque local (`./uploads`). Les fichiers sont nommés avec UUID pour éviter les collisions.

**Endpoints** :

| Méthode | Chemin | Description |
|---|---|---|
| POST | `/api/media/upload` | Upload fichier (multipart) |
| POST | `/api/media/upload-base64` | Upload image base64 |
| GET | `/api/media/{filename}` | Téléchargement (inline) |
| DELETE | `/api/media/{filename}` | Suppression |

**Sécurité** : Vérification anti-path-traversal (`file.startsWith(uploadPath)`). Pas de validation MIME du contenu réel.

### 3.6 Notification Service — Port 8085

**Statut** : IMPLÉMENTÉ.

**Fonctionnalités** : Envoi d'emails (Resend API + Thymeleaf) avec retry (3 tentatives, backoff exponentiel). 5 templates : intervention-notification, password-reset, welcome, intervention-assigned, intervention-completed. Push notifications Firebase (conditional).

**Push Service :**
- `PushServiceInterface` — interface partagée
- `PushService` — implémentation Firebase Admin SDK (`@ConditionalOnProperty("firebase.enabled")`)
- `PushServiceNoop` — fallback quand Firebase désactivé (default)
- Toggle : `firebase.enabled=false` (default) / `firebase.enabled=true` + `firebase.service-account-path`
- `PushController` : `/api/push/register-token`, `/api/push/send`, `/api/push/health`

**Endpoints** :

| Méthode | Chemin | Description |
|---|---|---|
| POST | `/api/notifications/email` | Envoi email (202 ACCEPTED) |
| POST | `/api/push/register-token` | Enregistrer token FCM |
| POST | `/api/push/send` | Envoyer notification push |
| GET | `/api/push/health` | Health check push service |

### 3.7 Report Service — Port 8086

**Statut** : IMPLÉMENTÉ.

**Fonctionnalités** : Export CSV, PDF (OpenPDF), analytics agrégées. Circuit breaker + retry sur l'appel à intervention-service. Templates PDF et email (CRUD).

**Endpoints** :

| Méthode | Chemin | Description |
|---|---|---|
| GET | `/api/reports/interventions/csv` | Export CSV |
| GET | `/api/reports/interventions/pdf` | Export PDF |
| GET | `/api/reports/analytics` | Statistiques agrégées |
| GET | `/api/report/pdf-templates` | Liste templates PDF |
| POST | `/api/report/pdf-templates` | Créer template PDF |
| PUT | `/api/report/pdf-templates/{id}` | Modifier template PDF |
| DELETE | `/api/report/pdf-templates/{id}` | Supprimer template PDF |
| GET | `/api/report/email-templates` | Liste templates email |
| POST | `/api/report/email-templates` | Créer template email |
| PUT | `/api/report/email-templates/{id}` | Modifier template email |
| DELETE | `/api/report/email-templates/{id}` | Supprimer template email |

---

## 4. Sécurité

### Authentification

- **Keycloak** (port 8088) comme fournisseur OAuth2
- Realm : `ng-fields`
- Clients : `ng-fields-frontend` (frontend Angular/Mobile), `ng-fields-backend` (admin API)
- JWT avec claim `realm_access.roles` → mappé en `ROLE_*` Spring Security

### Rôles et permissions

| Rôle | Lectures | Écritures |
|---|---|---|
| ADMIN | Toutes | Toutes (incluant gestion entreprises) |
| MANAGER | Toutes | Toutes sauf admin users |
| TECHNICIAN | Interventions assignées, clients | Interventions assignées uniquement |
| CLIENT_ADMIN | Interventions entreprise, utilisateurs entreprise | Créer/modifier/supprimer utilisateurs entreprise |
| CLIENT_USER | Interventions entreprise | Profil uniquement |
| CLIENT_VIEWER | Interventions entreprise (lecture seule) | Aucune |

### Flux d'authentification

```
Client → Gateway → [JWT] → Service → Keycloak (validation JWT)
                  ↓
            Rôles extraits du JWT → @PreAuthorize
```

### Configuration CORS

Origines autorisées : `http://localhost:4200` (Angular), `http://localhost:8100` (Mobile).

---

## 5. Base de données

### Schémas

| Schéma | Service | Tables |
|---|---|---|
| `auth` | Auth | `users`, `audit_logs`, `companies`, `company_users`, `company_access_log` |
| `client` | Client | `clients`, `contacts` |
| `intervention` | Intervention | `interventions`, `intervention_items`, `intervention_photos` |

### Gestion du schéma

Le schéma est géré par **Hibernate `ddl-auto: update`**. Les tables sont créées automatiquement au premier démarrage de chaque service. Pas de Flyway.

Les migrations Flyway ont été supprimées du périmètre (décision : simplicité avec Hibernate).

---

## 6. Déploiement

### Dépendances externes

| Service | Dépend |
|---|---|
| Tous | PostgreSQL, Keycloak |
| Gateway | Redis (rate limiting) |
| Tous | — |

### Ports

| Service | Port | Profil |
|---|---|---|
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

---

## 7. Tests

### Tests unitaires — 65 tests (tous passent)

| Service | Fichier | Tests | Couverture |
|---------|---------|-------|-----------|
| intervention-service | `InterventionServiceTest` | 25 | CRUD, getStats, batch sync, GPS, SSE |
| intervention-service | `InterventionStatusServiceTest` | 16 | State machine, transitions |
| intervention-service | `ExportServiceTest` | 8 | CSV/HTML escaping |
| auth-service | `UserServiceTest` | 11 | CRUD, roles, changePassword |
| auth-service | `CompanyServiceTest` | 10 | CRUD, multi-tenant |
| notification-service | `PushServiceNoopTest` | 3 | Push fallback |

```bash
# Tous les tests
cd Backend && mvn test

# Un service
cd Backend/intervention-service && mvn test
```

---

## 8. État d'avancement

| Service | Statut | Remarques |
|---|---|---|
| Gateway | ✅ Fonctionnel | Rate limiting actif, circuit breaker, GlobalExceptionHandler (RFC 7807) |
| Auth | ✅ Fonctionnel | Keycloak synchronisé, audit trail, change-password, Resend email, RBAC CLIENT |
| Client | ✅ Fonctionnel | CRUD complet, recherche, soft delete, contacts |
| Intervention | ✅ Fonctionnel | Tous endpoints métier, state machine, sync batch, SSE, GPS, export |
| Media | ✅ Fonctionnel | Stockage fichier, MIME validation, sécurité renforcée |
| Notification | ✅ Fonctionnel | Email Resend, push Firebase (conditional), 5 templates |
| Report | ✅ Fonctionnel | CSV, PDF (OpenPDF), analytics, templates PDF/Email |
| Tests | ✅ 65 tests | Unit tests (JUnit 5 + Mockito), tous passent |

---

## 9. Décisions techniques

| Décision | Choix | Justification |
|---|---|---|
| Communication inter-services | RestClient | Pas de Feign (complexité inutile pour 5 services) |
| IDs | UUID | Scalable, pas de séquence, merge-friendly |
| Timestamps | OffsetDateTime | Timezone-aware, standard ISO 8601 |
| DTOs | Java records | Immutables, concis, `@Valid` natif |
| Synchro Keycloak/DB | Manuelle dans UserService | Pattern "write-through" |
| PDF | OpenPDF (LibrePDF) | Léger, pas de dépendance lourde |
| Schéma BDD | Hibernate ddl-auto: update | Simplicité, moins de maintenance que Flyway |
| Email | Resend API | API moderne, deliverability supérieure |
| Push | Firebase Admin SDK (conditional) | Toggle firebase.enabled, fallback noop |
| Logs | logstash-logback-encoder 8.0 | JSON structuré pour ELK/Grafana |
| Tests | JUnit 5 + Mockito | Standard Spring Boot |

---

## 10. Prochaines étapes

1. ~~**Tests** — Implémenter tests unitaires~~ ✅ 65 tests implémentés
2. ~~**Gestion Entreprises** — Implémenter les endpoints~~ ✅ US-037→043 complétées
3. ~~**Suppression Facturation** — Retirer les champs~~ ✅ Supprimé
4. **Média** — Migrer vers Supabase Storage / MinIO S3
5. **Séparation DB** — Utilisateurs PostgreSQL distincts par service
6. ~~**Documentation API** — Swagger agrégé via gateway~~ ✅ Annotations @Tag/@Operation/@ApiResponse
7. **Mobile Flutter** — Démarrer le développement de l'application mobile
8. **Frontend Angular** — Dashboard Manager, planning, écrans CRUD
9. **Tests d'intégration** — Testcontainers (PostgreSQL en mémoire)
10. **Tests E2E** — Auth → Client → Intervention → Photos → Signatures → PDF → Email
