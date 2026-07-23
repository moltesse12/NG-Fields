# Architecture du Backend NG-Fields

## 1. Présentation générale

Le backend NG-Fields est une architecture **microservices** (Spring Boot 4.1.0 / Java 25) sécurisée par **Keycloak** (OAuth2/JWT), avec une **API Gateway** comme point d'entrée unique et une base de données **PostgreSQL** avec un schéma dédié par service.

### Stack technique

| Composant | Technologie | Version |
|---|---|---|
| Runtime | Java | 25 |
| Framework | Spring Boot | 4.1.0 |
| Gateway | Spring Cloud Gateway (WebFlux) | 2025.1.2 |
| Auth | Keycloak (OAuth2/JWT) | 26.0.9 |
| Base de données | PostgreSQL | 18 |
| ORM | JPA / Hibernate | `ddl-auto: validate` |
| Migrations | Flyway | 11 (auth, client, intervention, report) |
| API Docs | SpringDoc OpenAPI | 3.0.3 |
| PDF | OpenPDF (LibrePDF) | 3.0.5 |
| Email | Resend API | — |
| Push | Firebase Admin SDK | 9.10.0 (conditional) |
| SSE | SseEmitter (Server-Sent Events) | — |
| Logs | Logback + logstash-logback-encoder | 8.0 |
| Rate limiting | Redis + Resilience4j | — |
| Brute force | Bucket4j (auth-service) | — |
| AV scanning | ClamAV client (media-service) | — |
| Tests | JUnit 5 + Mockito | 72 unit tests |

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
│   intervention, report       │
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

**Correlation ID** : Filtre manuel `CorrelationIdFilter` — header `X-Correlation-ID` propagé sur tous les services下游.

### 3.2 Auth Service — Port 8081

**Rôle** : Gestion des utilisateurs, rôles, authentification (délégation Keycloak), audit trail, gestion des entreprises.

**Tables** : `users` (copie locale des utilisateurs Keycloak), `audit_logs`, `companies`, `company_users`, `company_access_log`, `failed_login_attempts`.

**Email** : Resend API (via `EmailService`), templates Thymeleaf (intervention-notification, password-reset, welcome, intervention-assigned, intervention-completed).

**Sécurité** :
- **Brute force protection** : OWASP hybride — 10 tentatives échouées → blocage 30 minutes (`BruteForceProtectionService` + `FailedLoginAttempt` entity)
- **Email verification** : JWT stateless tokens (pas de table séparée) — `EmailVerificationService`
- **Rate limiting** : Bucket4j sur les endpoints auth (inscription, login, reset-password)
- **Audit trail** : `company_access_log` enregistre toutes les opérations utilisateur

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

**Fonctionnalités** :
- **Duplicate detection** : `ClientService` vérifie l'unicité du nom d'entreprise (`existsByCompanyNameIgnoreCase`) avant création/modification
- **Réactivation** : endpoint `POST /api/clients/{id}/reactivate` pour réactiver un client soft-deleted
- **Circuit breaker** : Resilience4j sur les appels vers intervention-service (timeouts 5s/10s, retries 3x, fallback sendFallback)
- **DTO validation** : `@Pattern` E.164 pour les numéros de téléphone

**Endpoints** :

| Méthode | Chemin | Accès | Description |
|---|---|---|---|
| POST | `/api/clients` | ADMIN | Création client |
| GET | `/api/clients` | ADMIN/MANAGER/TECHNICIAN | Liste paginée |
| GET | `/api/clients/search?q=` | ADMIN/MANAGER/TECHNICIAN | Recherche (nom, contact, email) |
| GET | `/api/clients/{id}` | ADMIN/MANAGER/TECHNICIAN | Détail client |
| PUT | `/api/clients/{id}` | ADMIN | Modification |
| DELETE | `/api/clients/{id}` | ADMIN | Désactivation (soft delete) |
| POST | `/api/clients/{id}/reactivate` | ADMIN | Réactivation client |

### 3.4 Intervention Service — Port 8083

**Rôle** : Cœur métier — gestion complète des interventions terrain.

**Tables** : `interventions`, `intervention_items`, `intervention_photos`.

**Fonctionnalités avancées :**
- **SSE Real-time** : `SseEmitterManager` + `EventController` — push temps réel des événements `INTERVENTION_STATUS_CHANGED`, `NEW_TICKET`
- **Dashboard Manager** : `DashboardController` + `DashboardService` — KPIs, stats par technicien/client, export CSV/Excel/HTML
- **Planning Manager** : `ManagerController` — planning hebdomadaire, affectation interventions
- **Export** : `ExportService` — CSV/HTML avec filtres (status, technicianId), escaping injection
- **Batch Sync** : `SyncController` + `SyncService` — synchronisation hors-ligne, last-write-wins
- **Email** : `InterventionEmailService` (@Async, @Retry) — Resend API pour envoi rapports
- **GPS** : champs `gps_latitude`/`gps_longitude`, validation [-90,90], [-180,180]
- **Intervention Lock** : `InterventionLockManager` — verrouillage optimistic sur les opérations concurrentes
- **Schedule conflict detection** : `InterventionRepository` vérifie les chevauchements de planning
- **Photo cleanup** : `PhotoService.deleteAllPhotos()` supprime les fichiers media lors de la suppression d'une intervention

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
- Photos (avant/après, 5 max chaque) — **supprimées automatiquement** lors de la suppression intervention
- Pièces utilisées (type, description, quantité, prix unitaire)
- Synchronisation mobile (local_id)
- **Stripping métadonnées EXIF** : `ImageMetadataStripper` supprime les métadonnées GPS/appareil lors de l'upload

### 3.5 Media Service — Port 8084

**Rôle** : Stockage et distribution de fichiers (photos, signatures, PDF).

**Particularité** : Service sans base de données — stockage sur disque local (`./uploads`). Les fichiers sont nommés avec UUID pour éviter les collisions.

**Fonctionnalités** :
- **Antivirus scanning** : `AntivirusScanner` (ClamAV client) — analyse systématique de chaque upload
- **Image compression** : `ImageCompressor` — compression automatique des images > 1MB
- **Company quota** : `CompanyQuotaTracker` — quota par entreprise (configurable via `media.quota.max-per-company`)
- **Audit trail** : `FileAccessAuditLogger` — journalisation de chaque accès fichier (upload, download, delete)
- **File ownership** : vérification de l'appartenance du fichier avant suppression
- **Download auth** : endpoint GET nécessite un JWT valide

**Endpoints** :

| Méthode | Chemin | Description |
|---|---|---|
| POST | `/api/media/upload` | Upload fichier (multipart) |
| POST | `/api/media/upload-base64` | Upload image base64 |
| GET | `/api/media/{filename}` | Téléchargement (inline) |
| DELETE | `/api/media/{filename}` | Suppression |

**Sécurité** : Vérification anti-path-traversal, antivirus, MIME validation, quota entreprise, audit trail.

### 3.6 Notification Service — Port 8085

**Statut** : IMPLÉMENTÉ.

**Fonctionnalités** : Envoi d'emails (Resend API + Thymeleaf) avec retry (3 tentatives, backoff exponentiel). 5 templates : intervention-notification, password-reset, welcome, intervention-assigned, intervention-completed. Push notifications Firebase (conditional).

**Sécurité** :
- **Rate limiting** : `RateLimiter` (Bucket4j) — 50 emails/heure par destinataire
- **Dead Letter Queue** : `DeadLetterQueueService` — emails en échec après 3 tentatives → DLQ pour investigation
- **Email audit** : `EmailAuditLogger` — journalisation complète (destinataire, template, statut, erreur)
- **Send fallback** : en cas d'erreur Resend, fallback en log WARNING au lieu de crash

**Push Service :**
- `PushServiceInterface` — interface partagée
- `PushService` — implémentation Firebase Admin SDK (`@ConditionalOnProperty("firebase.enabled")`)
- `PushServiceNoop` — fallback quand Firebase désactivé (default)
- Toggle : `firebase.enabled=false` (default) / `firebase.enabled=true` + `firebase.service-account-path`
- `PushController` : `/api/push/register-token`, `/api/push/send`, `/api/push/health`
- **Error differentiation** : `PushService` distingue les erreurs FCM (token invalide vs. rate limit vs. réseau)

**Endpoints** :

| Méthode | Chemin | Description |
|---|---|---|
| POST | `/api/notifications/email` | Envoi email (202 ACCEPTED) |
| POST | `/api/push/register-token` | Enregistrer token FCM |
| POST | `/api/push/send` | Envoyer notification push |
| GET | `/api/push/health` | Health check push service |

### 3.7 Report Service — Port 8086

**Statut** : IMPLÉMENTÉ.

**Fonctionnalités** : Export CSV, PDF (OpenPDF 3.0.5), analytics agrégées. Circuit breaker + retry sur l'appel à intervention-service. Templates PDF et email (CRUD).

**Sécurité** :
- **HTML sanitization** : `HtmlSanitizer` — strip `<script>`, event handlers (`onclick`, `onerror`), `javascript:`, `<iframe>`, `<object>`, `<embed>`, `<form>`, CSS `expression()`. Protege les champs `bodyHtml`, `name`, `description`, `subject` des templates email, et `name`, `description` des templates PDF.
- **JSON config validation** : `PdfTemplateService.validateJson()` valide le champ `config` des templates PDF

**Performance** :
- **Analytics caching** : `AnalyticsDto` en cache `AtomicReference` avec TTL 60s + `@Scheduled` refresh (pas de requête DB à chaque appel)
- **Streaming PDF** : déjà implémenté via `StreamingResponseBody` (non-bloquant)

**Endpoints** :

| Méthode | Chemin | Description |
|---|---|---|
| GET | `/api/reports/interventions/csv` | Export CSV |
| GET | `/api/reports/interventions/pdf` | Export PDF |
| GET | `/api/reports/analytics` | Statistiques agrégées (cache 60s) |
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

### Sécurité renforcée

- **Brute force protection** : OWASP hybride — 10 tentatives échouées → blocage 30 minutes (auth-service)
- **Email verification** : JWT stateless tokens (auth-service)
- **HTML sanitization** : strip script/event handlers sur les templates email/PDF (report-service)
- **Rate limiting** : Redis sur gateway + Bucket4j sur auth-service + rate limiter sur notification-service
- **Antivirus** : ClamAV sur tous les uploads (media-service)
- **File ownership** : vérification avant suppression (media-service)
- **Request/response logging** : `RequestResponseLoggingFilter` dans shared-lib (toggled via `LOG_REQUEST_RESPONSE=true`)
- **Secrets sanitization** : Actuator env endpoint filtre les clés sensibles
- **Optimistic locking** : `@Version` sur toutes les entités critiques

### Configuration CORS

Origines autorisées : `http://localhost:4200` (Angular), `http://localhost:8100` (Mobile).

---

## 5. Base de données

### Schémas

| Schéma | Service | Tables |
|---|---|---|
| `auth` | Auth | `users`, `audit_logs`, `companies`, `company_users`, `company_access_log`, `failed_login_attempts` |
| `client` | Client | `clients`, `contacts` |
| `intervention` | Intervention | `interventions`, `intervention_items`, `intervention_photos` |
| `report` | Report | `report_requests`, `pdf_templates`, `email_templates` |

### Gestion du schéma

Le schéma est géré par **Flyway** avec des migrations SQL (`V1__init.sql`) pour les services auth, client, intervention et report. Hibernate est en mode `ddl-auto: validate` — il ne crée ni ne modifie les tables, il vérifie uniquement la cohérence.

Les migrations Flyway s'exécutent automatiquement au démarrage de chaque service (`spring.flyway.baseline-on-migrate: true`).

### Index

Des index ont été ajoutés sur les colonnes de recherche fréquente :

| Service | Table | Index |
|---|---|---|
| auth | `users` | `idx_users_username`, `idx_users_company_id` |
| auth | `audit_logs` | `idx_audit_user_id`, `idx_audit_created_at` |
| client | `clients` | `idx_clients_email`, `idx_clients_active`, `idx_clients_company_id` |
| intervention | `interventions` | `idx_interventions_status`, `idx_interventions_technician_id`, `idx_interventions_created_at` |

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

### Tests unitaires — 72 tests (tous passent)

| Service | Fichier | Tests | Couverture |
|---------|---------|-------|-----------|
| intervention-service | `InterventionServiceTest` | 25 | CRUD, getStats, batch sync, GPS, SSE |
| intervention-service | `InterventionStatusServiceTest` | 16 | State machine, transitions |
| intervention-service | `ExportServiceTest` | 8 | CSV/HTML escaping |
| auth-service | `UserServiceTest` | 11 | CRUD, roles, changePassword |
| auth-service | `CompanyServiceTest` | 10 | CRUD, multi-tenant |
| notification-service | `PushServiceNoopTest` | 3 | Push fallback |
| report-service | `TemplateRenderingTest` | 7 | Email templates (5), PDF template config validation (2) |

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
| Gateway | ✅ Fonctionnel | Rate limiting actif, circuit breaker, GlobalExceptionHandler (RFC 7807), CorrelationIdFilter |
| Auth | ✅ Fonctionnel | Keycloak synchronisé, audit trail, change-password, Resend email, RBAC CLIENT, brute force protection, email verification |
| Client | ✅ Fonctionnel | CRUD complet, recherche, soft delete, contacts, duplicate detection, réactivation, circuit breaker |
| Intervention | ✅ Fonctionnel | Tous endpoints métier, state machine, sync batch, SSE, GPS, export, intervention lock, schedule conflict, photo cleanup, metadata stripping |
| Media | ✅ Fonctionnel | Stockage fichier, MIME validation, AV scanning, image compression, company quota, audit trail, download auth |
| Notification | ✅ Fonctionnel | Email Resend, push Firebase (conditional), 5 templates, DLQ, email audit, rate limiting |
| Report | ✅ Fonctionnel | CSV, PDF (OpenPDF 3.0.5), analytics (cache 60s), templates PDF/Email, HTML sanitization |
| Tests | ✅ 72 tests | Unit tests (JUnit 5 + Mockito), tous passent |

---

## 9. Décisions techniques

| Décision | Choix | Justification |
|---|---|---|
| Communication inter-services | RestClient | Pas de Feign (complexité inutile pour 5 services) |
| IDs | UUID | Scalable, pas de séquence, merge-friendly |
| Timestamps | OffsetDateTime | Timezone-aware, standard ISO 8601 |
| DTOs | Java records | Immutables, concis, `@Valid` natif |
| Synchro Keycloak/DB | Manuelle dans UserService | Pattern "write-through" |
| PDF | OpenPDF 3.0.5 | Léger, pas de dépendance lourde, dernière version stable |
| Schéma BDD | Flyway + Hibernate validate | Migrations traçables, validation au démarrage |
| Email | Resend API | API moderne, deliverability supérieure |
| Push | Firebase Admin SDK (conditional) | Toggle firebase.enabled, fallback noop |
| Logs | logstash-logback-encoder 8.0 | JSON structuré pour ELK/Grafana |
| Brute force | OWASP hybride (Bucket4j) | 10 tentatives → 30min lockout |
| AV scanning | ClamAV | Standard open-source, scanning systématique |
| Tests | JUnit 5 + Mockito | Standard Spring Boot |

---

## 10. Prochaines étapes

1. ~~**Tests** — Implémenter tests unitaires~~ ✅ 72 tests implémentés
2. ~~**Gestion Entreprises** — Implémenter les endpoints~~ ✅ US-037→043 complétées
3. ~~**Suppression Facturation** — Retirer les champs~~ ✅ Supprimé
4. ~~**Sécurité renforcée** — Brute force, AV scanning, HTML sanitization, audit~~ ✅ Implémenté
5. ~~**Migrations DB** — Flyway pour auth, client, intervention, report~~ ✅ Implémenté
6. **Média** — Migrer vers Supabase Storage / MinIO S3
7. **Séparation DB** — Utilisateurs PostgreSQL distincts par service
8. ~~**Documentation API** — Swagger agrégé via gateway~~ ✅ Annotations @Tag/@Operation/@ApiResponse
9. **Mobile Flutter** — Démarrer le développement de l'application mobile
10. **Frontend Angular** — Dashboard Manager, planning, écrans CRUD
11. **Tests d'intégration** — Testcontainers (PostgreSQL en mémoire)
12. **Tests E2E** — Auth → Client → Intervention → Photos → Signatures → PDF → Email
